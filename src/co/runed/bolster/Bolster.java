package co.runed.bolster;

import org.bukkit.plugin.java.JavaPlugin;

public class Bolster extends JavaPlugin {
    // SINGLETON INSTANCE
    private static Bolster instance;

    // GLOBAL ITEM REGISTRY FOR CUSTOM ITEMS
    private ItemRegistry itemRegistry;
    private CooldownManager cooldownManager;
    private ItemManager itemManager;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        this.itemRegistry = new ItemRegistry(this);
        this.cooldownManager = new CooldownManager(this);
        this.itemManager = new ItemManager(this);

        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public static Bolster getInstance() {
        return instance;
    }

    public static ItemRegistry getItemRegistry() {
        return Bolster.getInstance().itemRegistry;
    }

    public static CooldownManager getCooldownManager() {
        return Bolster.getInstance().cooldownManager;
    }

    public static ItemManager getItemManager() {
        return Bolster.getInstance().itemManager;
    }
}
