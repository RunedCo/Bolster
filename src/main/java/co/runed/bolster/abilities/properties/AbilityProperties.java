package co.runed.bolster.abilities.properties;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbilityProperties {
    public static final AbilityProperty<LivingEntity> CASTER = AbilityPropertyBuilder.key("caster").build();
    public static final AbilityProperty<List<LivingEntity>> TARGETS = AbilityPropertyBuilder.key("targets").initial(new ArrayList<LivingEntity>()).build();
    public static final AbilityProperty<World> WORLD = AbilityPropertyBuilder.key("world").build();
    public static final AbilityProperty<ItemStack> ITEM_STACK = AbilityPropertyBuilder.key("item_stack").build();
    public static final AbilityProperty<Block> BLOCK = AbilityPropertyBuilder.key("block").build();
    public static final AbilityProperty<Action> BLOCK_ACTION = AbilityPropertyBuilder.key("block_action").build();
    public static final AbilityProperty<BlockFace> BLOCK_FACE = AbilityPropertyBuilder.key("block_face").build();
    public static final AbilityProperty<Float> FORCE = AbilityPropertyBuilder.key("force").initial(0.0f).build();
    public static final AbilityProperty<Vector> VELOCITY = AbilityPropertyBuilder.key("velocity").initial(new Vector()).build();

    private final Map<AbilityProperty<?>, Object> values = new HashMap<>();

    public int size() {
        return this.values.size();
    }

    public boolean isEmpty() {
        return this.values.isEmpty();
    }

    public void clean() {
        this.values.clear();
    }

    public boolean containsKey(AbilityProperty<?> key) {
        return this.values.containsKey(key);
    }

    public <T> T get(AbilityProperty<T> key) {
        if(!this.values.containsKey(key)) return key.getDefault();

        return (T)this.values.get(key);
    }

    public <T> void set(AbilityProperty<T> key, T value) {
        this.values.put(key, value);
    }
}
