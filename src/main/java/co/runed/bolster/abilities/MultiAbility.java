package co.runed.bolster.abilities;

import co.runed.bolster.Bolster;
import co.runed.bolster.util.TimeUtil;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class MultiAbility extends Ability
{
    List<Ability> abilities = new ArrayList<>();
    boolean isSequence = false;

    public MultiAbility()
    {
        this(false);
    }

    public MultiAbility(boolean isSequence)
    {
        this.isSequence = isSequence;
    }

    public MultiAbility addAbility(BiConsumer<LivingEntity, Properties> func)
    {
        return this.addAbility(new LambdaAbility(func));
    }

    public MultiAbility addAbility(Ability ability)
    {
        this.abilities.add(ability);

        return this;
    }

    @Override
    public void setCaster(LivingEntity caster)
    {
        super.setCaster(caster);

        for (Ability ability : this.abilities)
        {
            ability.setCaster(caster);
        }
    }

    @Override
    public Duration getDuration()
    {
        Duration duration = Duration.ZERO;

        for (Ability ability : this.abilities)
        {
            duration = duration.plus(ability.getDuration());
        }

        return duration;
    }

    @Override
    public boolean isInProgress()
    {
        for (Ability ability : this.abilities)
        {
            if (ability.isInProgress())
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public void setTrigger(AbilityTrigger trigger)
    {
        super.setTrigger(trigger);

        for (Ability ability : this.abilities)
        {
            ability.setTrigger(trigger);
        }
    }

//    @Override
//    public boolean canActivate(Properties properties)
//    {
//        if (!super.canActivate(properties)) return false;
//
//        for (Ability ability : this.abilities)
//        {
//            boolean activate = ability.canActivate(properties);
//
//            if (!activate)
//            {
//                return false;
//            }
//        }
//
//        return true;
//    }

    @Override
    public void onActivate(Properties properties)
    {
        long ticks = 0;

        for (Ability ability : this.abilities)
        {
            if (isSequence)
            {
                ticks += TimeUtil.toTicks(ability.getDuration());
                Bukkit.getServer().getScheduler().runTaskLater(Bolster.getInstance(), () -> ability.activate(properties), ticks);
            }
            else
            {
                ability.activate(properties);
            }
        }
    }
}
