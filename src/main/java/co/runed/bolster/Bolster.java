package co.runed.bolster;

import co.runed.bolster.classes.TargetDummyClass;
import co.runed.bolster.commands.*;
import co.runed.bolster.common.redis.RedisChannels;
import co.runed.bolster.common.redis.payload.Payload;
import co.runed.bolster.common.redis.request.RegisterServerPayload;
import co.runed.bolster.common.redis.response.RegisterServerResponsePayload;
import co.runed.bolster.events.RedisMessageEvent;
import co.runed.bolster.fx.particles.ParticleSet;
import co.runed.bolster.game.GameMode;
import co.runed.bolster.game.Traits;
import co.runed.bolster.game.currency.Currencies;
import co.runed.bolster.game.currency.Currency;
import co.runed.bolster.managers.*;
import co.runed.bolster.status.*;
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
import org.bukkit.plugin.java.JavaPlugin;
import org.ipvp.canvas.MenuFunctionListener;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class Bolster extends JavaPlugin implements Listener
{
    // SINGLETON INSTANCE
    private static Bolster instance;

    private JedisPool jedisPool;

    private Warps warps;

    private CommandManager commandManager;
    private CooldownManager cooldownManager;
    private ItemManager itemManager;
    private AbilityManager abilityManager;
    private ManaManager manaManager;
    private SidebarManager sidebarManager;
    private ClassManager classManager;
    private StatusEffectManager statusEffectManager;
    private EntityManager entityManager;
    private PlayerManager playerManager;
    private EffectManager effectManager;

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
        this.itemManager = new ItemManager(this);
        this.abilityManager = new AbilityManager(this);
        this.sidebarManager = new SidebarManager(this);
        this.classManager = new ClassManager(this);
        this.manaManager = new ManaManager(this);
        this.statusEffectManager = new StatusEffectManager(this);
        this.playerManager = new PlayerManager(this);
        this.entityManager = new EntityManager(this);
        this.effectManager = new EffectManager(this);

        // SET MANA MANAGER SETTINGS
        this.manaManager.setDefaultMaximumMana(200);
        this.manaManager.setEnableXpManaBar(true);

        // REGISTER COMMANDS
        this.commandManager.add(new CommandItems());
        this.commandManager.add(new CommandItemsGUI());
        this.commandManager.add(new CommandBecome());
        this.commandManager.add(new CommandBecomeGUI());
        this.commandManager.add(new CommandMana());
        this.commandManager.add(new CommandSummonDummy());
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

        Registries.CLASSES.register("target_dummy", TargetDummyClass::new);
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

        /* Connect to Redis */
        this.jedisPool = new JedisPool(config.redisHost, config.redisPort);
        Bukkit.getScheduler().runTaskAsynchronously(this, this::setupRedisListener);

        getServer().getScheduler().scheduleSyncDelayedTask(this, this::onPostEnable);
    }

    public void onPostEnable()
    {
        setActiveGameMode(this.config.gameMode);

        GameMode gameMode = getActiveGameMode();

        RegisterServerPayload registerPayload = new RegisterServerPayload();
        registerPayload.serverId = this.serverId;
        registerPayload.status = gameMode.getStatus();
        registerPayload.gameMode = gameMode.getId();
        registerPayload.name = this.serverId;
        registerPayload.ipAddress = getServer().getIp();
        registerPayload.port = getServer().getPort();

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

    private void setupRedisListener()
    {
        Jedis subRedis = null;
        Jedis pubRedis = null;

        try
        {
            /* Creating Jedis object for connecting with redis server */
            subRedis = this.jedisPool.getResource();
            pubRedis = this.jedisPool.getResource();

            /* Creating JedisPubSub object for subscribing with channels */
            RedisManager redisManager = new RedisManager(this, subRedis, pubRedis);
        }

        catch (Exception ex)
        {
            System.out.println("Exception : " + ex.getMessage());
        }
        finally
        {
            if (subRedis != null)
            {
                subRedis.close();
            }

            if (pubRedis != null)
            {
                pubRedis.close();
            }
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
