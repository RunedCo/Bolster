package co.runed.bolster.abilities.base;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.Ability;
import co.runed.bolster.util.TaskUtil;
import co.runed.bolster.util.properties.Properties;

import java.time.Duration;
import java.util.function.Supplier;

public class RepeatingAbility extends Ability
{
    Duration duration = null;
    Supplier<Boolean> runUntil = null;
    long frequency;

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
    public void testActivate(Properties properties)
    {
        if (this.duration != null)
        {
            TaskUtil.runDurationTaskTimer(Bolster.getInstance(), () -> super.testActivate(properties), this.duration, frequency, frequency);
        }
        else
        {
            TaskUtil.runTaskTimerUntil(Bolster.getInstance(), () -> super.testActivate(properties), this.runUntil, frequency, frequency, null);
        }

        super.testActivate(properties);
    }

    @Override
    public void onActivate(Properties properties)
    {

    }
}
