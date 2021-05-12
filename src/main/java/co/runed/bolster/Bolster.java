package co.runed.bolster;

import co.runed.bolster.classes.TargetDummyClass;
import co.runed.bolster.commands.*;
import co.runed.bolster.util.properties.Property;
import co.runed.bolster.util.traits.Traits;
import co.runed.bolster.wip.BowUtil;
import co.runed.bolster.wip.DisguiseListener;
import co.runed.bolster.game.GameMode;
import co.runed.bolster.managers.*;
import co.runed.bolster.status.*;
import co.runed.bolster.util.registries.Registries;
import co.runed.bolster.util.registries.Registry;
import co.runed.bolster.util.currency.Currencies;
import co.runed.bolster.util.currency.Currency;
import co.runed.bolster.wip.PotionUtil;
import co.runed.bolster.wip.TestListener;
import co.runed.bolster.wip.particles.ParticleSet;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import de.slikey.effectlib.EffectManager;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.ipvp.canvas.MenuFunctionListener;

public class Bolster extends JavaPlugin implements Listener
{
    // SINGLETON INSTANCE
    private static Bolster instance;

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

    private MongoClient mongoClient;

    @Override
    public void onLoad()
    {
        instance = this;
    }

    @Override
    public void onEnable()
    {
        super.onEnable();

        try
        {
            this.config = new Config();
        }
        catch (Exception e)
        {
            this.getLogger().severe("FAILED TO LOAD CONFIG FILE");
            e.printStackTrace();
            this.setEnabled(false);
            return;
        }

        MongoCredential credential = MongoCredential.createCredential(this.config.databaseUsername, "admin", this.config.databasePassword.toCharArray());
        ConnectionString connectionString = new ConnectionString("mongodb://" + this.config.databaseUrl + ":" + this.config.databasePort);
        CodecRegistry pojoCodecRegistry = CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                pojoCodecRegistry);

        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .credential(credential)
                .codecRegistry(codecRegistry)
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .build();

        this.mongoClient = MongoClients.create(clientSettings);

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
        this.commandManager.add(new CommandBecome());
        this.commandManager.add(new CommandBecomeGUI());
        this.commandManager.add(new CommandMana());
        this.commandManager.add(new CommandSummonDummy());
        this.commandManager.add(new CommandConfirm());
        this.commandManager.add(new CommandSetItemLevel());
        this.commandManager.add(new CommandLevelItem());
        this.commandManager.add(new CommandPause());
        this.commandManager.add(new CommandCurrency());

        // REGISTER PLUGIN CHANNELS
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "bolster:disguise");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "bolster:undisguise");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "bolster:add_status_effect");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "bolster:remove_status_effect");

        Registries.CLASSES.register("target_dummy", TargetDummyClass::new);

        // CREATE REGISTRIES
        Registries.PARTICLE_SETS.register("bruce_test", ParticleSet::new);

        this.menuListener = new MenuFunctionListener();

        // REGISTER EVENTS
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(menuListener, this);
        Bukkit.getPluginManager().registerEvents(new TestListener(), this);
        Bukkit.getPluginManager().registerEvents(new DisguiseListener(), this);
        Bukkit.getPluginManager().registerEvents(new PotionUtil(), this);
        Bukkit.getPluginManager().registerEvents(new BowUtil(), this);

        this.registerStatusEffects();
        this.registerCurrencies();
        this.registerTraits();
    }

    // NOTE: SHIT WORKAROUND FOR CANVAS NOT TRIGERRING EVENT WHEN IN SPECTATOR
    @EventHandler(priority = EventPriority.HIGH)
    private void onInventoryClick(InventoryClickEvent event)
    {
        if(!event.isCancelled()) return;

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

    public void setActiveGameMode(GameMode gameMode)
    {
        if (this.activeGameMode != null)
        {
            HandlerList.unregisterAll(this.activeGameMode.getProperties());
            HandlerList.unregisterAll(this.activeGameMode);
        }

        this.activeGameMode = gameMode;

        Bukkit.getPluginManager().registerEvents(this.activeGameMode.getProperties(), this);
        Bukkit.getPluginManager().registerEvents(this.activeGameMode, this);

        this.activeGameMode.start();
    }

    public GameMode getActiveGameMode()
    {
        return this.activeGameMode;
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

    public static MongoClient getMongoClient()
    {
        return Bolster.getInstance().mongoClient;
    }
}
