package co.runed.bolster.managers;

import co.runed.bolster.Bolster;
import co.runed.bolster.events.entity.EntityCleanupEvent;
import co.runed.bolster.events.entity.EntitySetCooldownEvent;
import co.runed.bolster.events.player.LoadPlayerDataEvent;
import co.runed.bolster.events.player.SavePlayerDataEvent;
import co.runed.bolster.events.server.RedisMessageEvent;
import co.runed.bolster.game.GameMode;
import co.runed.bolster.game.GameModeData;
import co.runed.bolster.game.PlayerData;
import co.runed.bolster.gui.sidebar.Sidebar;
import co.runed.bolster.match.PlayerConnectMatchHistoryEvent;
import co.runed.bolster.util.BukkitUtil;
import co.runed.bolster.util.TimeUtil;
import co.runed.bolster.util.registries.Registries;
import co.runed.dayroom.gson.GsonUtil;
import co.runed.dayroom.redis.RedisChannels;
import co.runed.dayroom.redis.RedisManager;
import co.runed.dayroom.redis.payload.Payload;
import co.runed.dayroom.redis.request.RequestPlayerDataPayload;
import co.runed.dayroom.redis.request.UpdatePlayerDataPayload;
import co.runed.dayroom.redis.response.RequestPlayerDataResponsePayload;
import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class PlayerManager extends Manager {
    private static PlayerManager _instance;
    private final Gson gson;

    private Map<UUID, PlayerData> playerData = new HashMap<>();
    private Map<UUID, String> lastPlayerDataJson = new HashMap<>();

    private Map<String, Class<? extends GameModeData>> gameModeDataTypes = new HashMap<>();

    private Map<UUID, Integer> timeSinceDisconnect = new HashMap<>();
    private Map<UUID, Integer> playTime = new HashMap<>();

    private final long playTimeFrequency = 20L * 10;

    public PlayerManager(Plugin plugin) {
        super(plugin);

        this.gson = GsonUtil.create();

        var config = Bolster.getBolsterConfig();
        if (config.cleanupPlayers) {
            Bukkit.getScheduler().runTaskTimer(plugin, this::cleanupPlayers, 0L, config.cleanupFrequency);
        }

        if (config.autoSave) {
            Bukkit.getScheduler().runTaskTimer(plugin, this::saveAllPlayers, 0L, config.autoSaveFrequency);
        }

        Bukkit.getScheduler().runTaskTimer(plugin, this::updatePlayTime, 0L, playTimeFrequency);

        _instance = this;
    }

    private Map<UUID, Sidebar> playerSidebars = new HashMap<>();

    /**
     * Gets the player's active sidebar instance
     *
     * @param player the player
     * @return the sidebar
     */
    public Sidebar getSidebar(Player player) {
        if (!this.playerSidebars.containsKey(player.getUniqueId())) return null;

        return this.playerSidebars.get(player.getUniqueId());
    }

    /**
     * Set a player's active sidebar instance
     *
     * @param player  the player
     * @param sidebar the sidebar
     */
    public void setSidebar(Player player, Sidebar sidebar) {
        if (this.playerSidebars.containsKey(player.getUniqueId())) {
            this.playerSidebars.get(player.getUniqueId()).removePlayer(player);
        }

        sidebar.addPlayer(player);

        this.playerSidebars.put(player.getUniqueId(), sidebar);
    }

    /**
     * Clears a players active sidebar instance
     *
     * @param player the player
     */
    public void clearSidebar(Player player) {
        if (this.playerSidebars.containsKey(player.getUniqueId())) {
            this.playerSidebars.get(player.getUniqueId()).removePlayer(player);
        }

        this.playerSidebars.remove(player.getUniqueId());
    }

    public void addGameModeDataClass(Class<? extends GameMode> gameMode, Class<? extends GameModeData> dataClass) {
        this.addGameModeDataClass(Registries.GAME_MODES.getId(gameMode), dataClass);
    }

    public void addGameModeDataClass(String id, Class<? extends GameModeData> dataClass) {
        this.gameModeDataTypes.put(id, dataClass);
    }

    public Class<? extends GameModeData> getGameModeDataClass(String id) {
        if (!this.gameModeDataTypes.containsKey(id))
            return GameModeData.class;

        return this.gameModeDataTypes.get(id);
    }

    public PlayerData deserialize(String json) {
        return this.gson.fromJson(json, PlayerData.class);
    }

    public String serialize(PlayerData playerData) {
        return this.gson.toJson(playerData);
    }

    public PlayerData getPlayerData(Player player) {
        return this.getPlayerData(player.getUniqueId());
    }

    public PlayerData getPlayerData(UUID uuid) {
        if (this.playerData.containsKey(uuid)) return this.playerData.get(uuid);

        var data = new PlayerData();

        data.setUuid(uuid);

        this.playerData.put(uuid, data);

        return data;
    }

    public void load(Player player) {
        this.load(player.getUniqueId());
    }

    private void load(UUID uuid) {
        var payload = new RequestPlayerDataPayload();
        payload.uuid = uuid;

        RedisManager.getInstance().publish(RedisChannels.REQUEST_PLAYER_DATA, payload);
    }

    public void save(Player player) {
        var playerData = PlayerManager.getInstance().getPlayerData(player);
        playerData.setName(player.getName());

        this.save(playerData);
    }

    private void save(PlayerData data) {
        var playerData = this.runSave(data);
        var json = this.serialize(playerData);

        var payload = new UpdatePlayerDataPayload();
        payload.playerData.put(playerData.getUuid(), json);

        lastPlayerDataJson.put(playerData.getUuid(), json);

        if (hasChanged(playerData.getUuid(), playerData)) RedisManager.getInstance().publish(RedisChannels.UPDATE_PLAYER_DATA, payload);
    }

    private PlayerData runSave(PlayerData data) {
        /* Call Save Event */
        var event = BukkitUtil.triggerEvent(new SavePlayerDataEvent(data.getPlayer(), data));
        data = event.getPlayerData();

        data.saveGameModeData();

        return data;
    }

    public boolean hasChanged(UUID uuid, PlayerData newData) {
        return !lastPlayerDataJson.containsKey(uuid) || !lastPlayerDataJson.get(uuid).equals(serialize(newData));
    }

    public void saveAllPlayers() {
        if (this.playerData.size() <= 0) return;

        var payload = new UpdatePlayerDataPayload();

        for (var data : this.playerData.values()) {
            var updated = this.runSave(data);
            var json = this.serialize(updated);

            if (hasChanged(updated.getUuid(), updated)) {
                lastPlayerDataJson.put(updated.getUuid(), json);

                payload.playerData.put(updated.getUuid(), json);
            }
        }

        if (payload.playerData.size() > 0) RedisManager.getInstance().publish(RedisChannels.UPDATE_PLAYER_DATA, payload);
    }

    public Collection<PlayerData> getAllPlayerData() {
        return playerData.values();
    }

    public void cleanupPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            BukkitUtil.triggerEvent(new EntityCleanupEvent(player, false));
        }

        var config = Bolster.getBolsterConfig();

        for (var entry : new ArrayList<>(this.timeSinceDisconnect.entrySet())) {
            var uuid = entry.getKey();
            var value = entry.getValue() + config.cleanupFrequency;

            this.timeSinceDisconnect.put(uuid, value);

            if (value >= config.forceCleanupTime) {
                BukkitUtil.triggerEvent(new EntityCleanupEvent(uuid, false));
            }
        }
    }

    public int getTimeSinceDisconnect(Player player) {
        return this.timeSinceDisconnect.getOrDefault(player.getUniqueId(), 0);
    }

    public int getPlayTime(Player player) {
        return playTime.getOrDefault(player.getUniqueId(), 0);
    }

    private void updatePlayTime() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.playTime.put(player.getUniqueId(), (int) (playTime.getOrDefault(player.getUniqueId(), 0) + playTimeFrequency));
        }
    }

    @EventHandler
    private void onRedisMessage(RedisMessageEvent event) {
        if (event.getChannel().equals(RedisChannels.REQUEST_PLAYER_DATA_RESPONSE)) {
            var payload = Payload.fromJson(event.getMessage(), RequestPlayerDataResponsePayload.class);
            var playerData = this.deserialize(payload.playerData);

            /* Call Load Event */
            var loadEvent = BukkitUtil.triggerEvent(new LoadPlayerDataEvent(playerData.getPlayer(), playerData));
            playerData = loadEvent.getPlayerData();

            playerData.lastJoinTime = TimeUtil.now();

            this.playerData.put(playerData.getUuid(), playerData);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onPlayerJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();

        this.load(player);

        Bolster.getActiveGameMode().getMatchHistory().addEvent(new PlayerConnectMatchHistoryEvent(player));

        this.timeSinceDisconnect.remove(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerLeave(PlayerQuitEvent event) {
        var player = event.getPlayer();

        this.save(player);

        this.playerData.remove(player.getUniqueId());

        this.timeSinceDisconnect.put(player.getUniqueId(), 0);
    }

    @EventHandler
    private void onWorldSave(WorldSaveEvent event) {
        this.saveAllPlayers();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onSetCooldown(EntitySetCooldownEvent event) {
        var entity = event.getEntity();
        if (!event.isGlobal()) return;
        if (!(entity instanceof Player)) return;

        var playerData = this.getPlayerData(entity.getUniqueId());
        List<CooldownManager.CooldownData> cooldowns = new ArrayList<>(playerData.getGlobalCooldowns());
        cooldowns.removeIf(cd -> cd.isDone() || (cd.cooldownId.equals(event.getCooldownId()) && cd.slot == event.getSlot()));

        cooldowns.add(event.getCooldownData());

        playerData.setGlobalCooldowns(cooldowns);
    }

    @EventHandler
    private void onCleanupEntity(EntityCleanupEvent event) {
        if (event.isForced()) {
            this.timeSinceDisconnect.remove(event.getUniqueId());
        }
    }

    public static PlayerManager getInstance() {
        return _instance;
    }
}
