package co.runed.bolster.managers;

import co.runed.bolster.Bolster;
import co.runed.bolster.util.Manager;
import co.runed.bolster.util.PlayerData;
import co.runed.bolster.util.json.Exclude;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class PlayerManager extends Manager
{
    private static PlayerManager _instance;
    HashMap<UUID, PlayerData> playerData = new HashMap<>();
    private Class<? extends PlayerData> dataClass = PlayerData.class;
    private final Gson gson;

    public PlayerManager(Plugin plugin)
    {
        super(plugin);

        ExclusionStrategy excludeStrategy = new ExclusionStrategy()
        {
            @Override
            public boolean shouldSkipClass(Class<?> clazz)
            {
                return false;
            }

            @Override
            public boolean shouldSkipField(FieldAttributes field)
            {
                return field.getAnnotation(Exclude.class) != null;
            }
        };

        this.gson = new GsonBuilder()
                .setExclusionStrategies(excludeStrategy)
                .create();

        _instance = this;
    }

    public void setDataClass(Class<? extends PlayerData> dataClass)
    {
        this.dataClass = dataClass;
    }

    public PlayerData deserialize(String json)
    {
        return this.gson.fromJson(json, dataClass);
    }

    public PlayerData createNew()
    {
        PlayerData data = new PlayerData();

        try
        {
            Constructor<? extends PlayerData> constructor = this.dataClass.getConstructor();
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

    public PlayerData load(UUID uuid)
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

        this.playerData.put(uuid, data);

        return data;
    }

    public void save(Player player)
    {
        this.save(PlayerManager.getInstance().getPlayerData(player));
    }

    public void save(PlayerData data)
    {
        MongoClient mongoClient = Bolster.getMongoClient();
        MongoDatabase db = mongoClient.getDatabase(Bolster.getBolsterConfig().databaseName);
        MongoCollection<Document> collection = db.getCollection("players");
        Document query = new Document("uuid", data.getUuid().toString());

        Document document = Document.parse(gson.toJson(data));
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
    }

    @EventHandler
    private void onWorldSave(WorldSaveEvent event)
    {
        this.saveAllPlayers();
    }

    public static PlayerManager getInstance()
    {
        return _instance;
    }
}