package co.runed.bolster.managers;

import co.runed.bolster.Bolster;
import co.runed.bolster.Config;
import co.runed.bolster.events.CleanupEntityEvent;
import co.runed.bolster.events.LoadPlayerDataEvent;
import co.runed.bolster.events.RedisMessageEvent;
import co.runed.bolster.events.SavePlayerDataEvent;
import co.runed.bolster.events.entity.EntitySetCooldownEvent;
import co.runed.bolster.game.GameMode;
import co.runed.bolster.game.GameModeData;
import co.runed.bolster.game.PlayerData;
import co.runed.bolster.network.RedisManager;
import co.runed.bolster.network.redis.Payload;
import co.runed.bolster.network.redis.RedisChannels;
import co.runed.bolster.network.redis.request.RequestPlayerDataPayload;
import co.runed.bolster.network.redis.request.UpdatePlayerDataPayload;
import co.runed.bolster.network.redis.response.RequestPlayerDataResponsePayload;
import co.runed.bolster.util.BukkitUtil;
import co.runed.bolster.util.json.GsonUtil;
import co.runed.bolster.util.registries.Registries;
import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class PlayerManager extends Manager
{
    private static PlayerManager _instance;
    private Map<UUID, PlayerData> playerData = new HashMap<>();
    private final Gson gson;
    private Map<String, Class<? extends GameModeData>> gameModeDataTypes = new HashMap<>();
    private Map<UUID, Integer> timeSinceDisconnect = new HashMap<>();

    public PlayerManager(Plugin plugin)
    {
        super(plugin);

        this.gson = GsonUtil.create();

        Config config = Bolster.getBolsterConfig();
        if (config.cleanupPlayers)
        {
            Bukkit.getScheduler().runTaskTimer(plugin, this::cleanupPlayers, 0L, config.cleanupFrequency);
        }

        if (config.autoSave)
        {
            Bukkit.getScheduler().runTaskTimer(plugin, this::saveAllPlayers, 0L, config.autoSaveFrequency);
        }

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

    public String serialize(PlayerData playerData)
    {
        return this.gson.toJson(playerData);
    }

    public PlayerData getPlayerData(Player player)
    {
        return this.getPlayerData(player.getUniqueId());
    }

    public PlayerData getPlayerData(UUID uuid)
    {
        if (this.playerData.containsKey(uuid)) return this.playerData.get(uuid);

        PlayerData data = new PlayerData();

        data.setUuid(uuid);

        this.playerData.put(uuid, data);

        return data;
    }

    public void load(Player player)
    {
        this.load(player.getUniqueId());
    }

    private void load(UUID uuid)
    {
        RequestPlayerDataPayload payload = new RequestPlayerDataPayload();
        payload.uuid = uuid;

        RedisManager.getInstance().publish(RedisChannels.REQUEST_PLAYER_DATA, payload);
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
        SavePlayerDataEvent event = BukkitUtil.triggerEvent(new SavePlayerDataEvent(playerData.getPlayer(), playerData));
        playerData = event.getPlayerData();

        playerData.saveGameModeData();

        UpdatePlayerDataPayload payload = new UpdatePlayerDataPayload();
        payload.uuid = playerData.getUuid();
        payload.playerData = this.serialize(playerData);

        RedisManager.getInstance().publish(RedisChannels.UPDATE_PLAYER_DATA, payload);

//        MongoClient mongoClient = Bolster.getMongoClient();
//        MongoDatabase db = mongoClient.getDatabase(Bolster.getBolsterConfig().databaseName);
//        MongoCollection<Document> collection = db.getCollection("players");
//        Document query = new Document("uuid", playerData.getUuid().toString());
//
//        Document document = Document.parse(gson.toJson(playerData));
//        ReplaceOptions options = new ReplaceOptions();
//        options.upsert(true);
//        collection.replaceOne(query, document, options);
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

    public void cleanupPlayers()
    {
        for (Player player : Bukkit.getOnlinePlayers())
        {
            BukkitUtil.triggerEvent(new CleanupEntityEvent(player, false));
        }

        Config config = Bolster.getBolsterConfig();

        for (Map.Entry<UUID, Integer> entry : new ArrayList<>(this.timeSinceDisconnect.entrySet()))
        {
            UUID uuid = entry.getKey();
            int value = entry.getValue() + config.cleanupFrequency;

            this.timeSinceDisconnect.put(uuid, value);

            if (value >= config.forceCleanupTime)
            {
                BukkitUtil.triggerEvent(new CleanupEntityEvent(uuid, false));
            }
        }
    }

    @EventHandler
    private void onRedisMessage(RedisMessageEvent event)
    {
        if (event.getChannel().equals(RedisChannels.REQUEST_PLAYER_DATA_RESPONSE))
        {
            RequestPlayerDataResponsePayload payload = Payload.fromJson(event.getMessage(), RequestPlayerDataResponsePayload.class);
            PlayerData playerData = this.deserialize(payload.playerData);

            /* Call Load Event */
            LoadPlayerDataEvent loadEvent = BukkitUtil.triggerEvent(new LoadPlayerDataEvent(playerData.getPlayer(), playerData));
            playerData = loadEvent.getPlayerData();

            this.playerData.put(playerData.getUuid(), playerData);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onPlayerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();

        this.load(player);

        this.timeSinceDisconnect.remove(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerLeave(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();

        this.save(player);

        this.playerData.remove(player.getUniqueId());

        this.timeSinceDisconnect.put(player.getUniqueId(), 0);
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

    @EventHandler
    private void onCleanupEntity(CleanupEntityEvent event)
    {
        if (event.isForced())
        {
            this.timeSinceDisconnect.remove(event.getUniqueId());
        }
    }

    public static PlayerManager getInstance()
    {
        return _instance;
    }
}
