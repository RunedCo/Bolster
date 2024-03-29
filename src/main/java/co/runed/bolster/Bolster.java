package co.runed.bolster;

import co.runed.bolster.commands.*;
import co.runed.bolster.events.server.RedisMessageEvent;
import co.runed.bolster.events.server.ReloadConfigEvent;
import co.runed.bolster.fx.particles.ParticleSet;
import co.runed.bolster.game.GameMode;
import co.runed.bolster.game.GameProperties;
import co.runed.bolster.game.Settings;
import co.runed.bolster.game.currency.Currencies;
import co.runed.bolster.game.traits.Traits;
import co.runed.bolster.managers.*;
import co.runed.bolster.status.*;
import co.runed.bolster.util.BukkitUtil;
import co.runed.bolster.util.json.BukkitAwareObjectTypeAdapter;
import co.runed.bolster.util.json.InventorySerializableAdapter;
import co.runed.bolster.util.lang.Lang;
import co.runed.bolster.util.registries.Registries;
import co.runed.bolster.wip.*;
import co.runed.dayroom.ServerData;
import co.runed.dayroom.gson.GsonUtil;
import co.runed.dayroom.player.BasicPlayerInfo;
import co.runed.dayroom.redis.RedisChannels;
import co.runed.dayroom.redis.RedisManager;
import co.runed.dayroom.redis.payload.Payload;
import co.runed.dayroom.redis.request.ServerDataPayload;
import co.runed.dayroom.redis.request.UnregisterServerPayload;
import co.runed.dayroom.redis.response.ListServersResponsePayload;
import co.runed.dayroom.redis.response.RegisterServerResponsePayload;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import de.slikey.effectlib.EffectManager;
import me.libraryaddict.disguise.utilities.json.SerializerGameProfile;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.ipvp.canvas.MenuFunctionListener;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Bolster extends JavaPlugin implements Listener {
    // SINGLETON INSTANCE
    private static Bolster instance;

    private Warps warps;

    private CommandManager commandManager;
    private CooldownManager cooldownManager;
    private StatusEffectManager statusEffectManager;
    private EntityManager entityManager;
    private PlayerManager playerManager;
    private EffectManager effectManager;
    private RedisManager redisManager;
    private NPCManager npcManager;
    private ChatManager chatManager;
    private GlowSystem glowSystem;
    private DamageListener damageListener;

    private MenuFunctionListener menuListener;

    private Config config;

    private GameMode activeGameMode;
    private String serverId = null;
    private boolean hideServer;
    private Map<String, ServerData> servers = new HashMap<>();

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        this.warps = new Warps(this);

        this.loadConfig();

        hideServer = config.hidden;

        Lang.load(this);

        GsonUtil.addBuilderFunction(this::setupGson);

        // Create managers
        this.commandManager = new CommandManager();
        this.cooldownManager = new CooldownManager(this);
        this.statusEffectManager = new StatusEffectManager(this);
        this.playerManager = new PlayerManager(this);
        this.entityManager = new EntityManager(this);
        this.effectManager = new EffectManager(this);
        this.npcManager = new NPCManager(this);
        this.chatManager = new ChatManager(this);
        
        this.glowSystem = new GlowSystem(this);
        this.damageListener = new DamageListener(this);

        // Register Commands
        this.commandManager.add(new CommandBolster());
        this.commandManager.add(new CommandCurrency());
        this.commandManager.add(new CommandGame());
        this.commandManager.add(new CommandShop());
        this.commandManager.add(new CommandUnlock());
        this.commandManager.add(new CommandPremium());
        this.commandManager.add(new CommandWarp());
        this.commandManager.add(new CommandWarpGUI());
        this.commandManager.add(new CommandServerGUI());
        this.commandManager.add(new CommandMatch());

        // Register Plugin Channels
        var messenger = getServer().getMessenger();
        messenger.registerOutgoingPluginChannel(this, "BungeeCord");
        messenger.registerOutgoingPluginChannel(this, "bolster:disguise");
        messenger.registerOutgoingPluginChannel(this, "bolster:undisguise");
        messenger.registerOutgoingPluginChannel(this, "bolster:add_status_effect");
        messenger.registerOutgoingPluginChannel(this, "bolster:remove_status_effect");

        Registries.PARTICLE_SETS.register("bruce_test", ParticleSet::new);

        // Gui listener
        this.menuListener = new MenuFunctionListener();

        // Register events
        BukkitUtil.registerEvents(this, this, menuListener, new PotionSystem(), new BowTracker(), new CombatSystem(), new InventoryTracker(), new WorldGuardListener());

        // Initialize and register static objects
        Settings.initialize();
        GameProperties.initialize();
        Currencies.initialize();
        Traits.initialize();

        this.registerStatusEffects();

        // Redis
        var redisChannels = Arrays.asList(RedisChannels.LIST_SERVERS_RESPONSE, RedisChannels.REQUEST_PLAYER_DATA_RESPONSE, RedisChannels.REGISTER_SERVER_RESPONSE, RedisChannels.REQUEST_MATCH_HISTORY_ID_RESPONSE);
        this.redisManager = new RedisManager(config.redisHost, config.redisPort, null, null, redisChannels);
        this.redisManager.setDefaultTarget("proxy");
        this.redisManager.setMessageHandler((channel, message) -> BukkitUtil.triggerEventSync(new RedisMessageEvent(channel, message)));

        Bukkit.getScheduler().runTaskAsynchronously(this, redisManager::setup);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, this::onPostEnable);
    }

    private GsonBuilder setupGson(GsonBuilder existing) {
        return existing.registerTypeAdapterFactory(BukkitAwareObjectTypeAdapter.FACTORY)
                .registerTypeHierarchyAdapter(Inventory.class, new InventorySerializableAdapter())
                .registerTypeAdapter(WrappedGameProfile.class, new SerializerGameProfile())
                .registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer());
    }

    public void onPostEnable() {
        var firstWorld = Bukkit.getWorlds().get(0);
        firstWorld.setGameRule(GameRule.SPAWN_RADIUS, 1);
        firstWorld.setSpawnLocation(config.mapSpawn);

        setActiveGameMode(config.gameMode);

        var registerPayload = new ServerDataPayload();
        registerPayload.serverData = this.getServerData();

        RedisManager.getInstance().publish(RedisChannels.REGISTER_SERVER, registerPayload);
    }

    public void loadConfig() {
        try {
            this.config = new Config();

            serverId = Bolster.getInstance().config.serverId;
        }
        catch (Exception e) {
            this.getLogger().severe("FAILED TO LOAD CONFIG FILE");
            e.printStackTrace();
            this.setEnabled(false);
        }
    }

    public ServerData getServerData() {
        var gameMode = getActiveGameMode();

        var serverData = new ServerData();
        serverData.id = this.serverId;
        serverData.hidden = hideServer;
        serverData.status = gameMode.getStatus();
        serverData.gameMode = gameMode.getId();
        serverData.name = config.serverName;
        serverData.gameModeName = gameMode.getName();
        serverData.ipAddress = getServer().getIp();
        serverData.port = getServer().getPort();

        for (var player : Bukkit.getOnlinePlayers()) {
            serverData.onlinePlayers.add(new BasicPlayerInfo(player.getUniqueId(), player.getName()));
        }

        serverData.maxPlayers = Bukkit.getMaxPlayers();
        serverData.maxPremiumPlayers = config.premiumSlots;

        return serverData;
    }

    // NOTE: SHIT WORKAROUND FOR CANVAS NOT TRIGERRING EVENT WHEN IN SPECTATOR
    @EventHandler(priority = EventPriority.HIGH)
    private void onInventoryClick(InventoryClickEvent event) {
        if (!event.isCancelled()) return;

        this.menuListener.handleGuiClick(event);
    }

    private void registerStatusEffects() {
        var statusEffectRegistry = Registries.STATUS_EFFECTS;

        statusEffectRegistry.register("blind", BlindStatusEffect.class);
        statusEffectRegistry.register("grounded", GroundedStatusEffect.class);
        statusEffectRegistry.register("invulnerable", InvulnerableStatusEffect.class);
        statusEffectRegistry.register("root", RootStatusEffect.class);
        statusEffectRegistry.register("stun", StunStatusEffect.class);
        statusEffectRegistry.register("untargetable", UntargetableStatusEffect.class);
        statusEffectRegistry.register("knockback_resistance", KnockbackResistanceStatusEffect.class);
        statusEffectRegistry.register("vulnerable", VulnerableStatusEffect.class);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        var payload = new UnregisterServerPayload();
        payload.serverId = this.serverId;

        RedisManager.getInstance().publish(RedisChannels.UNREGISTER_SERVER, payload);

        PlayerManager.getInstance().saveAllPlayers();
    }

    @EventHandler
    private void onRedisMessage(RedisMessageEvent event) {
        if (event.getChannel().equals(RedisChannels.REGISTER_SERVER_RESPONSE)) {
            var payload = Payload.fromJson(event.getMessage(), RegisterServerResponsePayload.class);

            this.setServerId(payload.serverId);

            RedisManager.getInstance().setSenderId(this.getServerId());
        }

        if (event.getChannel().equals(RedisChannels.LIST_SERVERS_RESPONSE)) {
            var payload = Payload.fromJson(event.getMessage(), ListServersResponsePayload.class);
            this.servers = payload.servers;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerLeave(PlayerQuitEvent event) {
        var payload = new ServerDataPayload();
        payload.serverData = this.getServerData();
        payload.serverData.onlinePlayers.removeIf(data -> data.uuid.equals(event.getPlayer().getUniqueId()));

        RedisManager.getInstance().publish(RedisChannels.UPDATE_SERVER, payload);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerJoin(PlayerJoinEvent event) {
        Bolster.updateServer();
    }

    public void setServerId(String id) {
        serverId = id;
    }

    public String getServerId() {
        return serverId;
    }

    public Map<String, ServerData> getServers() {
        return Collections.unmodifiableMap(servers);
    }

    public static void debug(String out) {
        if (getBolsterConfig().debugMode) {
            getInstance().getLogger().info("[DEBUG] " + out);
        }
    }

    /* Gamemode */
    public static boolean isActiveGameMode(Class<? extends GameMode> gameMode) {
        return isActiveGameMode(Registries.GAME_MODES.getId(gameMode));
    }

    public static boolean isActiveGameMode(String gameMode) {
        var activeId = Bolster.getBolsterConfig().gameMode;
        var active = getActiveGameMode();

        if (active != null) activeId = active.getId();

        return activeId != null && activeId.equals(gameMode);
    }

    public static GameMode getActiveGameMode() {
        return Bolster.getInstance().activeGameMode;
    }

    public static void setActiveGameMode(String id) {
        var bolster = Bolster.getInstance();
        var gameMode = Registries.GAME_MODES.get(id);

        if (gameMode != null) {
            setActiveGameMode(gameMode);
        }
        else {
            bolster.getLogger().severe("Invalid Game Mode '" + id + "'");
        }
    }

    public static void setActiveGameMode(GameMode gameMode) {
        var bolster = Bolster.getInstance();

        if (bolster.activeGameMode != null) {
            bolster.getLogger().info("Unloading Game Mode '" + bolster.activeGameMode.getId() + "'");

            bolster.activeGameMode.stop();

            HandlerList.unregisterAll(bolster.activeGameMode.getProperties());
            HandlerList.unregisterAll(bolster.activeGameMode);
        }

        bolster.activeGameMode = gameMode;

        bolster.getLogger().info("Loading Game Mode '" + bolster.activeGameMode.getId() + "'");

        Bukkit.getPluginManager().registerEvents(bolster.activeGameMode.getProperties(), bolster);
        Bukkit.getPluginManager().registerEvents(bolster.activeGameMode, bolster);

        bolster.activeGameMode.start();
    }

    public static void setHidden(boolean hidden) {
        getInstance().hideServer = hidden;
        updateServer();
    }

    public static boolean isHidden() {
        return getInstance().hideServer;
    }

    public static void updateServer() {
        if (getActiveGameMode() == null) return;

        var payload = new ServerDataPayload();
        payload.serverData = Bolster.getInstance().getServerData();

        RedisManager.getInstance().publish(RedisChannels.UPDATE_SERVER, payload);
    }

    public static void reload() {
        BukkitUtil.triggerEvent(new ReloadConfigEvent());
    }

    // SINGLETON GETTERS
    public static Bolster getInstance() {
        return instance;
    }

    public static Config getBolsterConfig() {
        return Bolster.getInstance().config;
    }

    public static EffectManager getEffectManager() {
        return Bolster.getInstance().effectManager;
    }
}
