package co.runed.bolster;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.Condition;
import co.runed.bolster.abilities.conditions.AbilityOffCooldownCondition;
import co.runed.bolster.abilities.conditions.HasPermissionCondition;
import co.runed.bolster.abilities.conditions.HoldingItemCondition;
import co.runed.bolster.abilities.conditions.ItemOffCooldownCondition;
import co.runed.bolster.items.Item;
import co.runed.bolster.items.ItemSkin;
import co.runed.bolster.registries.ItemRegistry;
import co.runed.bolster.registries.Registry;
import org.bukkit.plugin.java.JavaPlugin;

public class Bolster extends JavaPlugin {
    // SINGLETON INSTANCE
    private static Bolster instance;

    // GLOBAL REGISTRIES FOR SERIALIZATION
    private Registry<Item> itemRegistry;
    private Registry<ItemSkin> itemSkinRegistry;
    private Registry<Ability> abilityRegistry;
    private Registry<Condition> conditionRegistry;

    public CommandManager commandManager;
    private CooldownManager cooldownManager;
    private ItemManager itemManager;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        this.itemRegistry = new ItemRegistry(this);
        this.abilityRegistry = new Registry<>(this);
        this.conditionRegistry = new Registry<>(this);

        this.commandManager = new CommandManager();
        this.cooldownManager = new CooldownManager(this);
        this.itemManager = new ItemManager(this);

        this.conditionRegistry.register("has_permission", HasPermissionCondition.class);
        this.conditionRegistry.register("holding_item", HoldingItemCondition.class);
        this.conditionRegistry.register("item_off_cooldown", ItemOffCooldownCondition.class);
        this.conditionRegistry.register("ability_off_cooldown", AbilityOffCooldownCondition.class);

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

    public static Registry<Ability> getAbilityRegistry() {
        return Bolster.getInstance().abilityRegistry;
    }

    public static Registry<Condition> getConditionRegistry() {
        return Bolster.getInstance().conditionRegistry;
    }

    public static CooldownManager getCooldownManager() {
        return Bolster.getInstance().cooldownManager;
    }

    public static ItemManager getItemManager() {
        return Bolster.getInstance().itemManager;
    }

    public static CommandManager getCommandManager() {
        return Bolster.getInstance().commandManager;
    }
}
