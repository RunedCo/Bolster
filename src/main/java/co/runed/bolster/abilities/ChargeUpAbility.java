package co.runed.bolster.abilities;

import co.runed.bolster.Bolster;
import co.runed.bolster.properties.Properties;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.time.Instant;

public abstract class ChargeUpAbility extends Ability
{
    int steps;
    double chargeTime;

    int currentStep = 0;
    Instant startTime;
    Instant endTime;
    BukkitTask chargeTask;
    boolean clicked = true;

    public ChargeUpAbility(double chargeTime, int steps)
    {
        super();

        this.setChargeTime(chargeTime);
        this.steps = steps;
    }

    public int getSteps()
    {
        return steps;
    }

    public void setSteps(int steps)
    {
        this.steps = steps;
    }

    public double getChargeTime()
    {
        return chargeTime;
    }

    public long getChargeTimeMs()
    {
        return (long) (this.getChargeTime() * 1000);
    }

    public void setChargeTime(double chargeTime)
    {
        this.chargeTime = chargeTime;
    }

    @Override
    public void onActivate(Properties properties)
    {
        this.clicked = true;

        if (this.chargeTask != null && !this.chargeTask.isCancelled()) return;

        this.startTime = Instant.now();
        this.endTime = this.startTime.plusMillis(this.getChargeTimeMs());

        this.chargeTask = Bukkit.getScheduler().runTaskTimer(Bolster.getInstance(), () -> {
            long msElapsed = Instant.now().minusMillis(this.startTime.toEpochMilli()).toEpochMilli();
            double percentCompleted = Math.min(msElapsed / (double) this.getChargeTimeMs(), 1);

            if (Instant.now().isAfter(this.endTime) || !this.clicked)
            {
                properties.set(AbilityProperties.CHARGE_TIME, msElapsed);
                properties.set(AbilityProperties.FORCE, (float) percentCompleted);

                this.onCharged(properties);

                this.currentStep = 0;
                this.chargeTask.cancel();
                return;
            }

            double percentSteps = this.currentStep / (double) this.steps;

            if (percentCompleted >= percentSteps)
            {
                this.onChargeStep(this.currentStep, properties);

                this.currentStep++;
            }

            this.clicked = false;
        }, 0L, 4L);
    }

    public abstract void onChargeStep(int step, Properties properties);

    public abstract void onCharged(Properties properties);
}
