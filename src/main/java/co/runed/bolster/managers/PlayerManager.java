package co.runed.bolster.managers;

import co.runed.bolster.Bolster;
import co.runed.bolster.Config;
import co.runed.bolster.common.gson.GsonUtil;
import co.runed.bolster.common.redis.RedisChannels;
import co.runed.bolster.common.redis.RedisManager;
import co.runed.bolster.common.redis.payload.Payload;
import co.runed.bolster.common.redis.request.RequestPlayerDataPayload;
import co.runed.bolster.common.redis.request.UpdatePlayerDataPayload;
import co.runed.bolster.common.redis.response.RequestPlayerDataResponsePayload;
import co.runed.bolster.events.entity.EntityCleanupEvent;
import co.runed.bolster.events.entity.EntitySetCooldownEvent;
import co.runed.bolster.events.player.LoadPlayerDataEvent;
import co.runed.bolster.events.player.SavePlayerDataEvent;
import co.runed.bolster.events.server.RedisMessageEvent;
import co.runed.bolster.game.GameMode;
import co.runed.bolster.game.GameModeData;
import co.runed.bolster.game.PlayerData;
import co.runed.bolster.util.BukkitUtil;
import co.runed.bolster.util.Manager;
import co.runed.bolster.util.TimeUtil;
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

    private void save(PlayerData data)
    {
        PlayerData playerData = this.runSave(data);

        UpdatePlayerDataPayload payload = new UpdatePlayerDataPayload();
        payload.playerData.put(playerData.getUuid(), this.serialize(playerData));

        RedisManager.getInstance().publish(RedisChannels.UPDATE_PLAYER_DATA, payload);
    }

    private PlayerData runSave(PlayerData data)
    {
        /* Call Save Event */
        SavePlayerDataEvent event = BukkitUtil.triggerEvent(new SavePlayerDataEvent(data.getPlayer(), data));
        data = event.getPlayerData();

        data.saveGameModeData();

        return data;
    }

    public void saveAllPlayers()
    {
        if (this.playerData.size() <= 0) return;

        UpdatePlayerDataPayload payload = new UpdatePlayerDataPayload();

        for (PlayerData data : this.playerData.values())
        {
            PlayerData updated = this.runSave(data);
            payload.playerData.put(updated.getUuid(), this.serialize(updated));
        }

        RedisManager.getInstance().publish(RedisChannels.UPDATE_PLAYER_DATA, payload);
    }

    public Collection<PlayerData> getAllPlayerData()
    {
        return playerData.values();
    }

    public void cleanupPlayers()
    {
        for (Player player : Bukkit.getOnlinePlayers())
        {
            BukkitUtil.triggerEvent(new EntityCleanupEvent(player, false));
        }

        Config config = Bolster.getBolsterConfig();

        for (Map.Entry<UUID, Integer> entry : new ArrayList<>(this.timeSinceDisconnect.entrySet()))
        {
            UUID uuid = entry.getKey();
            int value = entry.getValue() + config.cleanupFrequency;

            this.timeSinceDisconnect.put(uuid, value);

            if (value >= config.forceCleanupTime)
            {
                BukkitUtil.triggerEvent(new EntityCleanupEvent(uuid, false));
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

            playerData.lastJoinTime = TimeUtil.now();

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
    private void onCleanupEntity(EntityCleanupEvent event)
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
