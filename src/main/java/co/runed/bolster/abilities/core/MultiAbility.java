package co.runed.bolster.abilities.core;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.Ability;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.TimeUtil;
import org.bukkit.Bukkit;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

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

    public MultiAbility addAbility(Ability ability)
    {
        this.abilities.add(ability);

        return this;
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
    public void onActivate(Properties properties)
    {
        long ticks = 0;

        for (Ability ability : this.abilities)
        {
            if (isSequence) ticks += TimeUtil.toTicks(ability.getDuration());

            Bukkit.getServer().getScheduler().runTaskLater(Bolster.getInstance(), () -> ability.activate(properties), ticks);
        }
    }
}
