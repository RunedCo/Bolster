package co.runed.bolster;

import co.runed.bolster.commands.CommandItems;
import co.runed.bolster.commands.CommandMana;
import co.runed.bolster.items.Item;
import co.runed.bolster.items.ItemSkin;
import co.runed.bolster.managers.*;
import co.runed.bolster.properties.Properties;
import co.runed.bolster.registries.ItemRegistry;
import co.runed.bolster.registries.Registry;
import co.runed.bolster.properties.GameProperties;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.ipvp.canvas.MenuFunctionListener;

public class Bolster extends JavaPlugin {
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
    private ScoreboardManager scoreboardManager;

    private Properties gameProperties;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        super.onEnable();

        this.itemRegistry = new ItemRegistry(this);
        this.itemSkinRegistry = new Registry<>(this);

        this.commandManager = new CommandManager();
        this.cooldownManager = new CooldownManager(this);
        this.itemManager = new ItemManager(this);
        this.abilityManager = new AbilityManager(this);
        this.scoreboardManager = new ScoreboardManager(this);

        this.gameProperties = new GameProperties(this);

        this.manaManager = new ManaManager(this);

        this.manaManager.setDefaultMaximumMana(200);
        this.manaManager.setEnableXpManaBar(true);

        this.commandManager.add(new CommandItems());
        this.commandManager.add(new CommandMana());

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        Bukkit.getPluginManager().registerEvents(new MenuFunctionListener(), this);
        Bukkit.getPluginManager().registerEvents(new TestListener(), this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public static Bolster getInstance() {
        return instance;
    }

    public static Registry<Item> getItemRegistry() {
        return Bolster.getInstance().itemRegistry;
    }

    public static Registry<ItemSkin> getItemSkinRegistry() {
        return Bolster.getInstance().itemSkinRegistry;
    }

    public static CooldownManager getCooldownManager() {
        return Bolster.getInstance().cooldownManager;
    }

    public static ItemManager getItemManager() {
        return Bolster.getInstance().itemManager;
    }

    public static AbilityManager getAbilityManager() {
        return Bolster.getInstance().abilityManager;
    }

    public static ManaManager getManaManager() {
        return Bolster.getInstance().manaManager;
    }

    public static CommandManager getCommandManager() {
        return Bolster.getInstance().commandManager;
    }

    public static ScoreboardManager getScoreboardManager() {
        return Bolster.getInstance().scoreboardManager;
    }

    public static Properties getGameProperties() {
        return Bolster.getInstance().gameProperties;
    }
}
