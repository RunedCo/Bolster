package co.runed.bolster;

import co.runed.bolster.commands.*;
import co.runed.bolster.events.server.RedisMessageEvent;
import co.runed.bolster.events.server.ReloadConfigEvent;
import co.runed.bolster.fx.particles.ParticleSet;
import co.runed.bolster.game.GameMode;
import co.runed.bolster.game.currency.Currencies;
import co.runed.bolster.game.traits.Traits;
import co.runed.bolster.managers.*;
import co.runed.bolster.status.*;
import co.runed.bolster.util.BukkitUtil;
import co.runed.bolster.util.chat.ChatManager;
import co.runed.bolster.util.config.ConfigUtil;
import co.runed.bolster.util.json.BukkitAwareObjectTypeAdapter;
import co.runed.bolster.util.json.InventorySerializableAdapter;
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
import com.mojang.authlib.properties.PropertyMap;
import de.slikey.effectlib.EffectManager;
import me.libraryaddict.disguise.utilities.json.SerializerGameProfile;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.ipvp.canvas.MenuFunctionListener;
import redis.clients.jedis.JedisPool;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Bolster extends JavaPlugin implements Listener {
    // SINGLETON INSTANCE
    private static Bolster instance;

    private JedisPool jedisPool;

    private Warps warps;

    private CommandManager commandManager;
    private CooldownManager cooldownManager;
    private SidebarManager sidebarManager;
    private StatusEffectManager statusEffectManager;
    private EntityManager entityManager;
    private PlayerManager playerManager;
    private EffectManager effectManager;
    private RedisManager redisManager;
    private NPCManager npcManager;
    private ChatManager chatManager;
    private GlowSystem glowSystem;

    private MenuFunctionListener menuListener;

    private Config config;
    private Map<String, String> lang = new HashMap<>();

    private GameMode activeGameMode;
    private String serverId = null;
    private Map<String, ServerData> servers = new HashMap<>();

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        super.onEnable();

        this.loadConfig();

        loadLang(this);

        this.setupGson();

        this.warps = new Warps(this);

        // CREATE MANAGERS
        this.commandManager = new CommandManager();
        this.cooldownManager = new CooldownManager(this);
        this.sidebarManager = new SidebarManager(this);
        this.statusEffectManager = new StatusEffectManager(this);
        this.playerManager = new PlayerManager(this);
        this.entityManager = new EntityManager(this);
        this.effectManager = new EffectManager(this);
        this.npcManager = new NPCManager(this);
        this.chatManager = new ChatManager(this);
        this.glowSystem = new GlowSystem(this);

        // REGISTER COMMANDS
        this.commandManager.add(new CommandBolster());

        this.commandManager.add(new CommandCurrency());
        this.commandManager.add(new CommandGame());
        this.commandManager.add(new CommandShop());
        this.commandManager.add(new CommandUnlock());
        this.commandManager.add(new CommandPremium());

        this.commandManager.add(new CommandWarp());
        this.commandManager.add(new CommandWarpGUI());

        this.commandManager.add(new CommandServerGUI());

        // REGISTER PLUGIN CHANNELS
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "bolster:disguise");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "bolster:undisguise");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "bolster:add_status_effect");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "bolster:remove_status_effect");

        Registries.PARTICLE_SETS.register("bruce_test", ParticleSet::new);

        this.menuListener = new MenuFunctionListener();

        // REGISTER EVENTS
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(menuListener, this);
        Bukkit.getPluginManager().registerEvents(new DisguiseListener(), this);
        Bukkit.getPluginManager().registerEvents(new PotionSystem(), this);
        Bukkit.getPluginManager().registerEvents(new BowTracker(), this);
        Bukkit.getPluginManager().registerEvents(new CombatTracker(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryTracker(), this);
        Bukkit.getPluginManager().registerEvents(new WorldGuardListener(), this);

        this.registerStatusEffects();
        this.registerCurrencies();
        this.registerTraits();

        var redisChannels = Arrays.asList(RedisChannels.LIST_SERVERS_RESPONSE, RedisChannels.REQUEST_PLAYER_DATA_RESPONSE, RedisChannels.REGISTER_SERVER_RESPONSE);
        this.redisManager = new RedisManager(config.redisHost, config.redisPort, null, null, redisChannels);
        this.redisManager.setDefaultTarget("proxy");
        this.redisManager.setMessageHandler((channel, message) -> BukkitUtil.triggerEventSync(new RedisMessageEvent(channel, message)));

        Bukkit.getScheduler().runTaskAsynchronously(this, redisManager::setup);

        getServer().getScheduler().scheduleSyncDelayedTask(this, this::onPostEnable);
    }

    private void setupGson() {
        GsonUtil.addBuilderFunction((gsonBuilder ->
                gsonBuilder.registerTypeAdapterFactory(BukkitAwareObjectTypeAdapter.FACTORY)
                        .registerTypeHierarchyAdapter(Inventory.class, new InventorySerializableAdapter())
                        .registerTypeAdapter(WrappedGameProfile.class, new SerializerGameProfile())
                        .registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer()))
        );
    }

    public ServerData getServerData() {
        var gameMode = getActiveGameMode();

        var serverData = new ServerData();
        serverData.id = this.serverId;
        serverData.status = gameMode.getStatus();
        serverData.gameMode = gameMode.getId();
        serverData.name = this.serverId;
        serverData.ipAddress = getServer().getIp();
        serverData.port = getServer().getPort();

        for (var player : Bukkit.getOnlinePlayers()) {
            serverData.onlinePlayers.add(new BasicPlayerInfo(player.getUniqueId(), player.getName()));
        }

        serverData.maxPlayers = Bukkit.getMaxPlayers();
        serverData.maxPremiumPlayers = config.premiumSlots;

        return serverData;
    }

    public void onPostEnable() {
        setActiveGameMode(this.config.gameMode);

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

    private void registerCurrencies() {
        var currencyRegistry = Registries.CURRENCIES;

        currencyRegistry.register(Currencies.DIAMOND);
        currencyRegistry.register(Currencies.EMERALD);
        currencyRegistry.register(Currencies.GOLD);
    }

    private void registerTraits() {
        var registry = Registries.TRAITS;

        registry.register(Traits.DEBUG_MODE);
        registry.register(Traits.COOLDOWN_REDUCTION_PERCENT);
        registry.register(Traits.MAX_HEALTH);
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

    public void loadLang(Plugin plugin) {
        try {
            var langFile = new File(plugin.getDataFolder(), "lang.yml");

            if (!langFile.exists()) plugin.saveResource("lang.yml", false);

            var langConfig = new YamlConfiguration();
            langConfig.load(langFile);

            lang.putAll(ConfigUtil.toStringMap(langConfig, true));
        }
        catch (IOException | InvalidConfigurationException e) {
            getLogger().severe("Error loading lang file for plugin " + plugin.getName());
        }
    }

    public Map<String, String> getLang() {
        return Collections.unmodifiableMap(lang);
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

    public static void updateServer() {
        if (getActiveGameMode() == null) return;

        var payload = new ServerDataPayload();
        payload.serverData = Bolster.getInstance().getServerData();

        RedisManager.getInstance().publish(RedisChannels.UPDATE_SERVER, payload);
    }

    public static void reload() {
        BukkitUtil.triggerEvent(new ReloadConfigEvent());
    }
}
