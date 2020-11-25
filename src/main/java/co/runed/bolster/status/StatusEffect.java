package co.runed.bolster.status;

import co.runed.bolster.Bolster;
import co.runed.bolster.managers.StatusEffectManager;
import co.runed.bolster.util.TaskUtil;
import co.runed.bolster.util.TimeUtil;
import co.runed.bolster.util.registries.IRegisterable;
import co.runed.bolster.util.registries.Registries;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public abstract class StatusEffect implements Listener, IRegisterable
{
    String id;
    double duration;
    LivingEntity entity;
    BukkitTask task;

    boolean active;

    public StatusEffect(double duration)
    {
        this.setDuration(duration);

        Bukkit.getPluginManager().registerEvents(this, Bolster.getInstance());
    }

    public abstract String getName();

    public ChatColor getColor()
    {
        return ChatColor.WHITE;
    }

    public Collection<Class<? extends StatusEffect>> getOverrides()
    {
        return new ArrayList<>();
    }

    public double getDuration()
    {
        return duration;
    }

    public void setDuration(double duration)
    {
        this.duration = duration;
    }

    public void addDuration(double duration)
    {
        this.duration += duration;
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

    public boolean isActive()
    {
        return this.active;
    }

    public void start(LivingEntity entity)
    {
        this.setEntity(entity);

        if (this.canStart())
        {
            this.onStart();
        }

        this.task = TaskUtil.runDurationTaskTimer(Bolster.getInstance(), this::onTick,
                TimeUtil.fromSeconds(this.getDuration()), 0, 1L, this::end);
    }

    public void end()
    {
        // TODO CHECK IF OTHER STATUS EFFECTS THAT ARE ACTIVE ARE USING EFFECTS AND IF SO DO NOT CANCEL
        for (PotionEffectType potionEffect : this.getPotionEffects())
        {
            this.getEntity().removePotionEffect(potionEffect);
        }

        StatusEffectManager.getInstance().removeStatusEffect(this.getEntity(), this);

        HandlerList.unregisterAll(this);

        this.onEnd();
    }

    public void clear()
    {
        if (this.task == null || this.task.isCancelled()) return;

        this.task.cancel();

        this.end();
    }

    @Override
    public String getId()
    {
        return Registries.STATUS_EFFECTS.getId(this);
    }

    @Override
    public String getDescription()
    {
        return this.getName();
    }

    @Override
    public void create(ConfigurationSection config)
    {

    }

    public abstract Collection<PotionEffectType> getPotionEffects();

    public abstract void onStart();

    public abstract void onEnd();

    public abstract void onTick();
}
