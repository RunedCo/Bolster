package co.runed.bolster.util.target;

import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.BolsterEntity;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.registries.IRegisterable;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
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
