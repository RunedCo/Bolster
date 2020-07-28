package co.runed.bolster.status;

import co.runed.bolster.Bolster;
import co.runed.bolster.util.DurationUtil;
import co.runed.bolster.util.TaskUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

public abstract class StatusEffect implements Listener
{
    int strength;
    double duration;
    LivingEntity entity;
    BukkitTask task;

    public StatusEffect(int strength, int duration)
    {
        this.setStrength(strength);
        this.setDuration(duration);

        Bukkit.getPluginManager().registerEvents(this, Bolster.getInstance());
    }

    public double getDuration()
    {
        return duration;
    }

    public void setDuration(double duration)
    {
        this.duration = duration;
    }

    public int getStrength()
    {
        return strength;
    }

    public void setStrength(int strength)
    {
        this.strength = strength;
    }

    public LivingEntity getEntity()
    {
        return entity;
    }

    public void setEntity(LivingEntity entity)
    {
        this.entity = entity;
    }

    public boolean canStart()
    {
        return true;
    }

    public void start(LivingEntity entity)
    {
        this.setEntity(entity);

        if (this.canStart())
        {
            this.onStart();
        }

        this.task = TaskUtil.runDurationTaskTimer(Bolster.getInstance(), this::onTick,
                DurationUtil.fromSeconds(this.getDuration()), 0, 1L, this::end);
    }

    public void end()
    {
        Bolster.getStatusEffectManager().removeStatusEffect(this.getEntity(), this);

        HandlerList.unregisterAll(this);

        this.onEnd();
    }

    public void clear()
    {
        if (this.task == null || this.task.isCancelled()) return;

        this.task.cancel();
    }

    public abstract void onStart();

    public abstract void onEnd();

    public abstract void onTick();
}
