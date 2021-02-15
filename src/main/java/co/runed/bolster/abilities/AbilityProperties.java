package co.runed.bolster.abilities;

import co.runed.bolster.BolsterEntity;
import co.runed.bolster.items.Item;
import co.runed.bolster.util.properties.FunctionProperty;
import co.runed.bolster.util.properties.Property;
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * List of variables passed to an ability when cast
 */
public class AbilityProperties
{
    // GENERAL PROPERTIES
    public static final Property<BolsterEntity> CASTER = new Property<>("caster");
    public static final Property<Entity> TARGET = new Property<>("target", null);
    public static final Property<Entity> INITIAL_TARGET = new Property<>("initial_target", null);
    public static final Property<World> WORLD = new Property<>("world");
    public static final Property<Event> EVENT = new Property<>("event");
    public static final Property<ItemStack> ITEM_STACK = new Property<>("item_stack");
    public static final Property<Item> ITEM = new Property<>("item");
    public static final Property<String> ITEM_ID = new Property<>("item_id");
    public static final Property<Integer> ITEM_COUNT = new Property<>("item_count");
    public static final Property<AbilityTrigger> TRIGGER = new Property<>("trigger", AbilityTrigger.ALL);
    public static final Property<AbilityProvider> ABILITY_PROVIDER = new Property<>("ability_provider");
    public static final Property<Ability> ABILITY = new Property<>("ability");

    // BLOCK PROPERTIES
    public static final Property<Block> BLOCK = new Property<>("block");
    public static final Property<Action> BLOCK_ACTION = new Property<>("block_action");
    public static final Property<BlockFace> BLOCK_FACE = new Property<>("block_face");

    // PROJECTILE PROPERTIES
    public static final Property<Float> FORCE = new Property<>("force", 0.0f);
    public static final Property<Vector> VELOCITY = new Property<>("velocity", new Vector());

    // FISHING PROPERTIES
    public static final Property<Entity> CAUGHT = new Property<>("caught");
    public static final Property<FishHook> HOOK = new Property<>("hook");
    public static final Property<PlayerFishEvent.State> FISH_STATE = new Property<>("fish_state");

    // DAMAGE PROPERTIES
    public static final Property<Double> DAMAGE = new Property<>("damage", 0.0d);
    public static final Property<EntityDamageEvent.DamageCause> DAMAGE_CAUSE = new Property<>("damage_cause", EntityDamageEvent.DamageCause.CUSTOM);
    public static final Property<Double> FINAL_DAMAGE = new Property<>("final_damage", 0.0d);
    public static final Property<Entity> DAMAGER = new Property<>("damager");

    // KILL PROPERTIES
    public static final Property<List<ItemStack>> DROPS = new Property<>("drops", new ArrayList<>());

    // CHARGE PROPERTIES
    public static final Property<Long> CHARGE_TIME = new Property<>("charge_time");

    // CLICK PROPERTIES
    public static final Property<ItemStack> CURRENT_ITEM_STACK = new Property<>("current_item_stack");
    public static final Property<Integer> SLOT = new Property<>("slot");
    public static final Property<Integer> RAW_SLOT = new Property<>("raw_slot");
    public static final Property<InventoryType.SlotType> SLOT_TYPE = new Property<>("slot_type");
    public static final Property<ClickType> CLICK_TYPE = new Property<>("click_type");
    public static final Property<InventoryAction> INVENTORY_ACTION = new Property<>("inventory_action");
    public static final Property<Inventory> INVENTORY = new Property<>("inventory");

    // ARMOR EQUIP PROPERTIES
    public static final Property<PlayerArmorChangeEvent.SlotType> ARMOR_SLOT = new Property<>("armor_slot");

    // EVENT PROPERTIES
    public static final Property<Boolean> IS_CANCELLED = new FunctionProperty<>("is_cancelled",
            (p) -> p.get(EVENT) instanceof Cancellable && ((Cancellable) p.get(EVENT)).isCancelled(),
            (p, c) -> {
                if (p.get(EVENT) instanceof Cancellable) ((Cancellable) p.get(EVENT)).setCancelled(c);
            });
}
