package co.runed.bolster.util.target;

import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.entity.BolsterEntity;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.registries.IRegisterable;
import co.runed.bolster.wip.CombatTracker;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.function.Function;

public class Target<T> implements IRegisterable
{
    public static final Target<BolsterEntity> CASTER = new Target<>("caster", properties -> properties.get(AbilityProperties.CASTER));
    public static final Target<BolsterEntity> TARGET = new Target<>("target", properties -> {
        Entity e = properties.get(AbilityProperties.TARGET);

        if (!(e instanceof LivingEntity)) return null;

        return BolsterEntity.from((LivingEntity) e);
    });

    public static final Target<BolsterEntity> INITIAL_TARGET = new Target<>("initial_target", properties -> {
        Entity e = properties.get(AbilityProperties.INITIAL_TARGET);

        if (!(e instanceof LivingEntity)) return null;

        return BolsterEntity.from((LivingEntity) e);
    });

    public static final Target<Block> BLOCK = new Target<>("block", properties -> properties.get(AbilityProperties.BLOCK));

    public static final Target<Location> CASTER_LOCATION = new Target<>("caster_location", properties -> properties.get(AbilityProperties.CASTER).getLocation());
    public static final Target<Location> TARGET_LOCATION = new Target<>("target_location", properties -> properties.get(AbilityProperties.TARGET).getLocation());
    public static final Target<Location> INITIAL_TARGET_LOCATION = new Target<>("initial_target_location", properties -> properties.get(AbilityProperties.INITIAL_TARGET).getLocation());
    public static final Target<Location> CASTER_EYE_LOCATION = new Target<>("caster_eye_location", properties -> properties.get(AbilityProperties.CASTER).getBukkit().getEyeLocation());
    public static final Target<Location> BLOCK_LOCATION = new Target<>("block_location", properties -> properties.get(AbilityProperties.BLOCK).getLocation());

    public static final Target<World> CASTER_WORLD = new Target<>("caster_world", properties -> Target.CASTER_LOCATION.get(properties).getWorld());
    public static final Target<World> TARGET_WORLD = new Target<>("target_world", properties -> Target.TARGET_LOCATION.get(properties).getWorld());

    public static final Target<BolsterEntity> CASTER_LAST_HIT = new Target<>("caster_last_hit", properties -> BolsterEntity.from(CombatTracker.getLastHit(Target.CASTER.get(properties).getBukkit())));
    public static final Target<BolsterEntity> TARGET_LAST_HIT = new Target<>("target_last_hit", properties -> BolsterEntity.from(CombatTracker.getLastHit(Target.TARGET.get(properties).getBukkit())));
    public static final Target<BolsterEntity> INITIAL_TARGET_LAST_HIT = new Target<>("initial_target_last_hit", properties -> BolsterEntity.from(CombatTracker.getLastHit(Target.INITIAL_TARGET.get(properties).getBukkit())));

    String id;
    Function<Properties, ? extends T> getTargetFunction;

    public Target(String id, Function<Properties, ? extends T> getTargetFunction)
    {
        this.getTargetFunction = getTargetFunction;
        this.id = id;
    }

    public T get(Properties properties)
    {
        return this.getTargetFunction.apply(properties);
    }

    public void setId(String id)
    {
        this.id = id;
    }

    @Override
    public String getId()
    {
        return this.id;
    }

    @Override
    public String getDescription()
    {
        return null;
    }
}
