package co.runed.bolster;

import co.runed.bolster.commands.CommandItems;
import co.runed.bolster.commands.CommandLightLevel;
import co.runed.bolster.commands.CommandMana;
import co.runed.bolster.commands.CommandModelData;
import co.runed.bolster.items.Item;
import co.runed.bolster.items.ItemSkin;
import co.runed.bolster.managers.*;
import co.runed.bolster.properties.GameProperties;
import co.runed.bolster.properties.Properties;
import co.runed.bolster.registries.Registry;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.ipvp.canvas.MenuFunctionListener;

public class Bolster extends JavaPlugin
{
    // SINGLETON INSTANCE
    private static Bolster instance;

    // GLOBAL REGISTRIES FOR SERIALIZATION
    private Registry<Item> itemRegistry;
    private Registry<ItemSkin> itemSkinRegistry;

    private CommandManager commandManager;
    private CooldownManager cooldownManager;
    private ItemManager itemManager;
    private AbilityManager abilityManager;
    private ManaManager manaManager;
    private SidebarManager sidebarManager;
    private ClassManager classManager;
    private StatusEffectManager statusEffectManager;

    private Properties gameProperties;

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
        this.itemRegistry = new Registry<>(this);
        this.itemSkinRegistry = new Registry<>(this);

        // CREATE MANAGERS
        this.commandManager = new CommandManager();
        this.cooldownManager = new CooldownManager(this);
        this.itemManager = new ItemManager(this);
        this.abilityManager = new AbilityManager(this);
        this.sidebarManager = new SidebarManager(this);
        this.classManager = new ClassManager(this);
        this.manaManager = new ManaManager(this);
        this.statusEffectManager = new StatusEffectManager(this);

        // CREATE GAME PROPERTIES
        this.gameProperties = new GameProperties(this);

        // SET MANA MANAGER SETTINGS
        this.manaManager.setDefaultMaximumMana(200);
        this.manaManager.setEnableXpManaBar(true);

        // REGISTER COMMANDS
        this.commandManager.add(new CommandItems());
        this.commandManager.add(new CommandMana());
        this.commandManager.add(new CommandModelData());
        this.commandManager.add(new CommandLightLevel());

        // REGISTER BUNGEECORD PLUGIN CHANNEL
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        // REGISTER MENU EVENTS
        Bukkit.getPluginManager().registerEvents(new MenuFunctionListener(), this);
        Bukkit.getPluginManager().registerEvents(new TestListener(), this);
    }

    @Override
    public void onDisable()
    {
        super.onDisable();
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

    public static Registry<ItemSkin> getItemSkinRegistry()
    {
        return Bolster.getInstance().itemSkinRegistry;
    }

    public static CooldownManager getCooldownManager()
    {
        return Bolster.getInstance().cooldownManager;
    }

    public static ItemManager getItemManager()
    {
        return Bolster.getInstance().itemManager;
    }

    public static AbilityManager getAbilityManager()
    {
        return Bolster.getInstance().abilityManager;
    }

    public static ManaManager getManaManager()
    {
        return Bolster.getInstance().manaManager;
    }

    public static CommandManager getCommandManager()
    {
        return Bolster.getInstance().commandManager;
    }

    public static SidebarManager getSidebarManager()
    {
        return Bolster.getInstance().sidebarManager;
    }

    public static ClassManager getClassManager()
    {
        return Bolster.getInstance().classManager;
    }

    public static StatusEffectManager getStatusEffectManager()
    {
        return Bolster.getInstance().statusEffectManager;
    }

    public static Properties getGameProperties()
    {
        return Bolster.getInstance().gameProperties;
    }
}
