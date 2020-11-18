package co.runed.bolster;

import co.runed.bolster.classes.BolsterClass;
import co.runed.bolster.classes.TargetDummyClass;
import co.runed.bolster.commands.*;
import co.runed.bolster.events.ArmorListener;
import co.runed.bolster.game.GameMode;
import co.runed.bolster.items.Item;
import co.runed.bolster.items.ItemSkin;
import co.runed.bolster.managers.*;
import co.runed.bolster.particles.ParticleSet;
import co.runed.bolster.upgrade.Upgrade;
import co.runed.bolster.util.properties.Property;
import co.runed.bolster.util.registries.Registry;
import co.runed.bolster.wip.TestListener;
import de.slikey.effectlib.EffectManager;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.ipvp.canvas.MenuFunctionListener;

public class Bolster extends JavaPlugin
{
    // SINGLETON INSTANCE
    private static Bolster instance;

    // GLOBAL REGISTRIES FOR SERIALIZATION
    private Registry<Item> itemRegistry;
    private Registry<BolsterClass> classRegistry;
    private Registry<ItemSkin> itemSkinRegistry;
    private Registry<ParticleSet> particleSetRegistry;
    private Registry<Upgrade> upgradeRegistry;
    private Registry<Property> gamePropertiesRegistry;

    private CommandManager commandManager;
    private CooldownManager cooldownManager;
    private ItemManager itemManager;
    private AbilityManager abilityManager;
    private ManaManager manaManager;
    private SidebarManager sidebarManager;
    private ClassManager classManager;
    private StatusEffectManager statusEffectManager;
    private EntityManager entityManager;

    private EffectManager effectManager;

    private GameMode activeGameMode;

    @Override
    public void onLoad()
    {
        instance = this;
    }

    @Override
    public void onEnable()
    {
        super.onEnable();

        // CREATE REGISTRIES
        this.itemRegistry = new Registry<>(this, "items");
        this.classRegistry = new Registry<>(this, "classes");
        this.itemSkinRegistry = new Registry<>(this);
        this.particleSetRegistry = new Registry<>(this);
        this.upgradeRegistry = new Registry<>(this);
        this.gamePropertiesRegistry = new Registry<>(this);

        this.particleSetRegistry.register("bruce_test", ParticleSet::new);

        // CREATE MANAGERS
        this.commandManager = new CommandManager();
        this.cooldownManager = new CooldownManager(this);
        this.itemManager = new ItemManager(this);
        this.abilityManager = new AbilityManager(this);
        this.sidebarManager = new SidebarManager(this);
        this.classManager = new ClassManager(this);
        this.manaManager = new ManaManager(this);
        this.statusEffectManager = new StatusEffectManager(this);

        this.entityManager = new EntityManager(this);

        this.effectManager = new EffectManager(this);

        // SET MANA MANAGER SETTINGS
        this.manaManager.setDefaultMaximumMana(200);
        this.manaManager.setEnableXpManaBar(true);

        // REGISTER COMMANDS
        this.commandManager.add(new CommandItems());
        this.commandManager.add(new CommandBecome());
        this.commandManager.add(new CommandMana());
        this.commandManager.add(new CommandSummonDummy());
        this.commandManager.add(new CommandConfirm());

        // REGISTER BUNGEECORD PLUGIN CHANNEL
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        this.classRegistry.register("target_dummy", TargetDummyClass::new);

        // REGISTER MENU EVENTS
        Bukkit.getPluginManager().registerEvents(new MenuFunctionListener(), this);
        Bukkit.getPluginManager().registerEvents(new TestListener(), this);
        //Bukkit.getPluginManager().registerEvents(new ArmorListener(), this);
    }

    @Override
    public void onDisable()
    {
        super.onDisable();
    }

    public void setActiveGameMode(GameMode gameMode)
    {
        if(this.activeGameMode != null)
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

    public static Registry<Item> getItemRegistry()
    {
        return Bolster.getInstance().itemRegistry;
    }

    public static Registry<BolsterClass> getClassRegistry()
    {
        return Bolster.getInstance().classRegistry;
    }

    public static Registry<ItemSkin> getItemSkinRegistry()
    {
        return Bolster.getInstance().itemSkinRegistry;
    }

    public static Registry<ParticleSet> getParticleSetRegistry()
    {
        return Bolster.getInstance().particleSetRegistry;
    }

    public static Registry<Upgrade> getUpgradeRegistry()
    {
        return Bolster.getInstance().upgradeRegistry;
    }

    public static EffectManager getEffectManager()
    {
        return Bolster.getInstance().effectManager;
    }
}
