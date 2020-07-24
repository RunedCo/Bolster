package co.runed.bolster.abilities;

import co.runed.bolster.items.Item;
import co.runed.bolster.properties.Property;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerFishEvent;
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
    public static final Property<LivingEntity> CASTER = new Property<>("caster");
    public static final Property<List<LivingEntity>> TARGETS = new Property<>("targets", new ArrayList<>());
    public static final Property<World> WORLD = new Property<>("world");
    public static final Property<Event> EVENT = new Property<>("event");
    public static final Property<ItemStack> ITEM_STACK = new Property<>("item_stack");
    public static final Property<Item> ITEM = new Property<>("item");

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
    public static final Property<Double> DAMAGE = new Property<>("double", 0.0d);

    // KILL PROPERTIES
    public static final Property<List<ItemStack>> DROPS = new Property<>("drops", new ArrayList<>());
}
