package co.runed.bolster;

import co.runed.bolster.commands.CommandItems;
import co.runed.bolster.commands.CommandMana;
import co.runed.bolster.items.Item;
import co.runed.bolster.items.ItemSkin;
import co.runed.bolster.managers.CommandManager;
import co.runed.bolster.managers.CooldownManager;
import co.runed.bolster.managers.ItemManager;
import co.runed.bolster.managers.ManaManager;
import co.runed.bolster.registries.ItemRegistry;
import co.runed.bolster.registries.Registry;
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
    private ManaManager manaManager;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        this.itemRegistry = new ItemRegistry(this);
        this.itemSkinRegistry = new Registry<>(this);

        this.commandManager = new CommandManager();
        this.cooldownManager = new CooldownManager(this);
        this.itemManager = new ItemManager(this);
        this.manaManager = new ManaManager(this);

        this.commandManager.add(new CommandItems());
        this.commandManager.add(new CommandMana());

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        Bukkit.getPluginManager().registerEvents(new MenuFunctionListener(), this);

        super.onEnable();
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

    public static ManaManager getManaManager() {
        return Bolster.getInstance().manaManager;
    }

    public static CommandManager getCommandManager() {
        return Bolster.getInstance().commandManager;
    }
}
