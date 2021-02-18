package co.runed.bolster.abilities.base;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.util.TaskUtil;
import co.runed.bolster.util.properties.Properties;

import java.time.Duration;
import java.util.function.Supplier;

public class RepeatingAbility extends Ability
{
    Duration duration = null;
    Supplier<Boolean> runUntil = null;
    int repeats = 0;
    long frequency;

    public RepeatingAbility(int repeats, long frequency)
    {
        this(frequency);

        this.repeats = repeats;
    }

    public RepeatingAbility(Duration duration, long frequency)
    {
        this(frequency);

        this.duration = duration.minus(Duration.ofSeconds(frequency / 20));
    }

    public RepeatingAbility(Supplier<Boolean> runUntil, long frequency)
    {
        this(frequency);

        this.runUntil = runUntil;
    }

    private RepeatingAbility(long frequency)
    {
        this.frequency = frequency;
    }

    @Override
    public Duration getDuration()
    {
        if (this.duration == null) return super.getDuration();

        return this.duration;
    }

    @Override
    public void doActivate(Properties properties)
    {
        if (this.duration != null)
        {
            TaskUtil.runDurationTaskTimer(Bolster.getInstance(), () -> this.run(properties), this.duration, frequency, frequency);
        }
        else if (this.runUntil != null)
        {
            TaskUtil.runTaskTimerUntil(Bolster.getInstance(), () -> this.run(properties), this.runUntil, frequency, frequency, null);
        }
        else if (this.repeats > 0)
        {
            TaskUtil.runRepeatingTaskTimer(Bolster.getInstance(), () -> this.run(properties), this.repeats, frequency, frequency);
        }

        properties.set(AbilityProperties.RUN_NUMBER, -1);
        this.run(properties);
    }

    private void run(Properties properties)
    {
        properties.set(AbilityProperties.RUN_NUMBER, properties.get(AbilityProperties.RUN_NUMBER) + 1);
        super.doActivate(properties);
    }

    @Override
    public void onActivate(Properties properties)
    {

    }
}
