package co.runed.bolster;

import co.runed.bolster.commands.*;
import co.runed.bolster.common.ServerData;
import co.runed.bolster.common.redis.RedisChannels;
import co.runed.bolster.common.redis.RedisManager;
import co.runed.bolster.common.redis.payload.Payload;
import co.runed.bolster.common.redis.request.ServerDataPayload;
import co.runed.bolster.common.redis.request.UnregisterServerPayload;
import co.runed.bolster.common.redis.response.RegisterServerResponsePayload;
import co.runed.bolster.events.RedisMessageEvent;
import co.runed.bolster.fx.particles.ParticleSet;
import co.runed.bolster.game.GameMode;
import co.runed.bolster.game.currency.Currencies;
import co.runed.bolster.game.currency.Currency;
import co.runed.bolster.game.traits.Traits;
import co.runed.bolster.managers.*;
import co.runed.bolster.status.*;
import co.runed.bolster.util.BukkitUtil;
import co.runed.bolster.util.properties.Property;
import co.runed.bolster.util.registries.Registries;
import co.runed.bolster.util.registries.Registry;
import co.runed.bolster.wip.*;
import de.slikey.effectlib.EffectManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.ipvp.canvas.MenuFunctionListener;
import redis.clients.jedis.JedisPool;

import java.util.Arrays;

public class Bolster extends JavaPlugin implements Listener
{
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

    private MenuFunctionListener menuListener;

    private Config config;

    private GameMode activeGameMode;
    public String serverId = null;

    @Override
    public void onLoad()
    {
        instance = this;
    }

    @Override
    public void onEnable()
    {
        super.onEnable();

        this.loadConfig();

        this.warps = new Warps(this);

        // CREATE MANAGERS
        this.commandManager = new CommandManager();
        this.cooldownManager = new CooldownManager(this);
        this.sidebarManager = new SidebarManager(this);
        this.statusEffectManager = new StatusEffectManager(this);
        this.playerManager = new PlayerManager(this);
        this.entityManager = new EntityManager(this);
        this.effectManager = new EffectManager(this);

        // REGISTER COMMANDS
        this.commandManager.add(new CommandCurrency());
        this.commandManager.add(new CommandGame());
        this.commandManager.add(new CommandShop());
        this.commandManager.add(new CommandUnlock());
        this.commandManager.add(new CommandPremium());

        this.commandManager.add(new CommandWarp());
        this.commandManager.add(new CommandWarpGUI());

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

        var redisChannels = Arrays.asList(RedisChannels.REQUEST_SERVERS_RESPONSE, RedisChannels.REQUEST_PLAYER_DATA_RESPONSE, RedisChannels.REGISTER_SERVER_RESPONSE);
        this.redisManager = new RedisManager(config.redisHost, config.redisPort, null, null, redisChannels);
        this.redisManager.setDefaultTarget("proxy");
        this.redisManager.setMessageHandler((channel, message) -> BukkitUtil.triggerEventSync(new RedisMessageEvent(channel, message)));

        Bukkit.getScheduler().runTaskAsynchronously(this, redisManager::setup);

        getServer().getScheduler().scheduleSyncDelayedTask(this, this::onPostEnable);
    }

    public ServerData getServerData()
    {
        GameMode gameMode = getActiveGameMode();

        ServerData serverData = new ServerData();
        serverData.id = this.serverId;
        serverData.status = gameMode.getStatus();
        serverData.gameMode = gameMode.getId();
        serverData.name = this.serverId;
        serverData.ipAddress = getServer().getIp();
        serverData.port = getServer().getPort();

        serverData.currentPlayers = Bukkit.getOnlinePlayers().size();
        serverData.maxPlayers = Bukkit.getMaxPlayers();
        serverData.maxPremiumPlayers = config.premiumSlots;

        return serverData;
    }

    public void onPostEnable()
    {
        setActiveGameMode(this.config.gameMode);

        ServerDataPayload registerPayload = new ServerDataPayload();
        registerPayload.serverData = this.getServerData();

        RedisManager.getInstance().publish(RedisChannels.REGISTER_SERVER, registerPayload);
    }

    public void loadConfig()
    {
        try
        {
            this.config = new Config();

            serverId = Bolster.getInstance().config.serverId;
        }
        catch (Exception e)
        {
            this.getLogger().severe("FAILED TO LOAD CONFIG FILE");
            e.printStackTrace();
            this.setEnabled(false);
        }
    }

    // NOTE: SHIT WORKAROUND FOR CANVAS NOT TRIGERRING EVENT WHEN IN SPECTATOR
    @EventHandler(priority = EventPriority.HIGH)
    private void onInventoryClick(InventoryClickEvent event)
    {
        if (!event.isCancelled()) return;

        this.menuListener.handleGuiClick(event);
    }

    private void registerStatusEffects()
    {
        Registry<StatusEffect> statusEffectRegistry = Registries.STATUS_EFFECTS;

        statusEffectRegistry.register("blind", BlindStatusEffect.class);
        statusEffectRegistry.register("grounded", GroundedStatusEffect.class);
        statusEffectRegistry.register("invulnerable", InvulnerableStatusEffect.class);
        statusEffectRegistry.register("root", RootStatusEffect.class);
        statusEffectRegistry.register("stun", StunStatusEffect.class);
        statusEffectRegistry.register("untargetable", UntargetableStatusEffect.class);
        statusEffectRegistry.register("knockback_resistance", KnockbackResistanceStatusEffect.class);
        statusEffectRegistry.register("vulnerable", VulnerableStatusEffect.class);
    }

    private void registerCurrencies()
    {
        Registry<Currency> currencyRegistry = Registries.CURRENCIES;

        currencyRegistry.register(Currencies.DIAMOND);
        currencyRegistry.register(Currencies.EMERALD);
        currencyRegistry.register(Currencies.GOLD);
    }

    private void registerTraits()
    {
        Registry<Property<?>> registry = Registries.TRAITS;

        registry.register(Traits.ATTACK_DAMAGE);
        registry.register(Traits.DEBUG_MODE);
        registry.register(Traits.MAX_HEALTH);
    }

    @Override
    public void onDisable()
    {
        super.onDisable();

        UnregisterServerPayload payload = new UnregisterServerPayload();
        payload.serverId = this.serverId;

        RedisManager.getInstance().publish(RedisChannels.UNREGISTER_SERVER, payload);

        PlayerManager.getInstance().saveAllPlayers();
    }

    @EventHandler
    private void onRedisMessage(RedisMessageEvent event)
    {
        if (event.getChannel().equals(RedisChannels.REGISTER_SERVER_RESPONSE))
        {
            RegisterServerResponsePayload payload = Payload.fromJson(event.getMessage(), RegisterServerResponsePayload.class);

            this.setServerId(payload.serverId);

            RedisManager.getInstance().setSenderId(this.getServerId());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerLeave(PlayerQuitEvent event)
    {
        ServerDataPayload payload = new ServerDataPayload();
        payload.serverData = this.getServerData();
        payload.serverData.currentPlayers -= 1;

        RedisManager.getInstance().publish(RedisChannels.UPDATE_SERVER, payload);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerJoin(PlayerJoinEvent event)
    {
        ServerDataPayload payload = new ServerDataPayload();
        payload.serverData = this.getServerData();

        RedisManager.getInstance().publish(RedisChannels.UPDATE_SERVER, payload);
    }

    public void setServerId(String id)
    {
        serverId = id;
    }

    public String getServerId()
    {
        return serverId;
    }

    // SINGLETON GETTERS
    public static Bolster getInstance()
    {
        return instance;
    }

    public static Config getBolsterConfig()
    {
        return Bolster.getInstance().config;
    }

    public static EffectManager getEffectManager()
    {
        return Bolster.getInstance().effectManager;
    }

    public static boolean isActiveGameMode(Class<? extends GameMode> gameMode)
    {
        return isActiveGameMode(Registries.GAME_MODES.getId(gameMode));
    }

    public static boolean isActiveGameMode(String gameMode)
    {
        String activeId = Bolster.getBolsterConfig().gameMode;
        GameMode active = getActiveGameMode();

        if (active != null) activeId = active.getId();

        return activeId != null && activeId.equals(gameMode);
    }

    public static GameMode getActiveGameMode()
    {
        return Bolster.getInstance().activeGameMode;
    }

    public static void setActiveGameMode(String id)
    {
        Bolster bolster = Bolster.getInstance();
        GameMode gameMode = Registries.GAME_MODES.get(id);

        if (gameMode != null)
        {
            setActiveGameMode(gameMode);
        }
        else
        {
            bolster.getLogger().severe("Invalid Game Mode '" + id + "'");
        }
    }

    public static void setActiveGameMode(GameMode gameMode)
    {
        Bolster bolster = Bolster.getInstance();

        if (bolster.activeGameMode != null)
        {
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
}
