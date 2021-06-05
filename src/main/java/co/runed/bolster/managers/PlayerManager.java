package co.runed.bolster.managers;

import co.runed.bolster.Bolster;
import co.runed.bolster.events.EntitySetCooldownEvent;
import co.runed.bolster.events.LoadPlayerDataEvent;
import co.runed.bolster.events.SavePlayerDataEvent;
import co.runed.bolster.game.GameMode;
import co.runed.bolster.game.GameModeData;
import co.runed.bolster.game.PlayerData;
import co.runed.bolster.util.json.GsonUtil;
import co.runed.bolster.util.registries.Registries;
import com.google.gson.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.util.*;

public class PlayerManager extends Manager
{
    private static PlayerManager _instance;
    private Map<UUID, PlayerData> playerData = new HashMap<>();
    private final Gson gson;
    private Map<String, Class<? extends GameModeData>> gameModeDataTypes = new HashMap<>();

    public PlayerManager(Plugin plugin)
    {
        super(plugin);

        this.gson = GsonUtil.create();

        _instance = this;
    }

    public void addGameModeDataClass(Class<? extends GameMode> gameMode, Class<? extends GameModeData> dataClass)
    {
        this.addGameModeDataClass(Registries.GAME_MODES.getId(gameMode), dataClass);
    }

    public void addGameModeDataClass(String id, Class<? extends GameModeData> dataClass)
    {
        this.gameModeDataTypes.put(id, dataClass);
    }

    public Class<? extends GameModeData> getGameModeDataClass(String id)
    {
        if (!this.gameModeDataTypes.containsKey(id))
            return GameModeData.class;

        return this.gameModeDataTypes.get(id);
    }

    public PlayerData deserialize(String json)
    {
        return this.gson.fromJson(json, PlayerData.class);
    }

    public PlayerData createNew()
    {
        PlayerData data = new PlayerData();

        try
        {
            Constructor<? extends PlayerData> constructor = PlayerData.class.getConstructor();
            data = constructor.newInstance();
        }
        catch (Exception e)
        {
        }

        return data;
    }

    public PlayerData getPlayerData(Player player)
    {
        return this.getPlayerData(player.getUniqueId());
    }

    public PlayerData getPlayerData(UUID uuid)
    {
        if (this.playerData.containsKey(uuid)) return this.playerData.get(uuid);

        PlayerData data = this.createNew();

        data.setUuid(uuid);

        this.playerData.put(uuid, data);

        return data;
    }

    public PlayerData load(Player player)
    {
        return this.load(player.getUniqueId());
    }

    private PlayerData load(UUID uuid)
    {
        MongoClient mongoClient = Bolster.getMongoClient();
        MongoDatabase db = mongoClient.getDatabase(Bolster.getBolsterConfig().databaseName);
        MongoCollection<Document> collection = db.getCollection("players");
        Document query = new Document("uuid", uuid.toString());

        PlayerData data = this.createNew();

        Document document = collection.find(query).first();

        if (document != null)
        {
            data = this.deserialize(document.toJson());
        }

        data.setUuid(uuid);

        /* Call Load Event */
        LoadPlayerDataEvent event = new LoadPlayerDataEvent(data.getPlayer(), data);
        Bukkit.getServer().getPluginManager().callEvent(event);
        data = event.getPlayerData();

        this.playerData.put(uuid, data);

        return data;
    }

    public void save(Player player)
    {
        PlayerData playerData = PlayerManager.getInstance().getPlayerData(player);
        playerData.setName(player.getName());

        this.save(playerData);
    }

    // TODO move to mongodb update vs just straight upsert (maybe via api)
    private void save(PlayerData data)
    {
        PlayerData playerData = data;

        /* Call Save Event */
        SavePlayerDataEvent event = new SavePlayerDataEvent(playerData.getPlayer(), playerData);
        Bukkit.getServer().getPluginManager().callEvent(event);
        playerData = event.getPlayerData();

        playerData.saveGameModeData();

        MongoClient mongoClient = Bolster.getMongoClient();
        MongoDatabase db = mongoClient.getDatabase(Bolster.getBolsterConfig().databaseName);
        MongoCollection<Document> collection = db.getCollection("players");
        Document query = new Document("uuid", playerData.getUuid().toString());

        Document document = Document.parse(gson.toJson(playerData));
        ReplaceOptions options = new ReplaceOptions();
        options.upsert(true);
        collection.replaceOne(query, document, options);
    }

    public void saveAllPlayers()
    {
        for (PlayerData data : this.playerData.values())
        {
            this.save(data);
        }
    }

    public Collection<PlayerData> getAllPlayerData()
    {
        return playerData.values();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlayerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();

        this.load(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerLeave(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();

        this.save(player);

        this.playerData.remove(player.getUniqueId());
    }

    @EventHandler
    private void onWorldSave(WorldSaveEvent event)
    {
        this.saveAllPlayers();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onSetCooldown(EntitySetCooldownEvent event)
    {
        LivingEntity entity = event.getEntity();
        if (!event.isGlobal()) return;
        if (!(entity instanceof Player)) return;

        PlayerData playerData = this.getPlayerData(entity.getUniqueId());
        List<CooldownManager.CooldownData> cooldowns = new ArrayList<>(playerData.getGlobalCooldowns());
        cooldowns.removeIf(cd -> cd.isDone() || (cd.cooldownId.equals(event.getCooldownId()) && cd.slot == event.getSlot()));

        cooldowns.add(event.getCooldownData());

        playerData.setGlobalCooldowns(cooldowns);
    }

    public static PlayerManager getInstance()
    {
        return _instance;
    }
}
