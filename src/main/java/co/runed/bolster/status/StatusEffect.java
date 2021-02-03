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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class StatusEffect implements Listener, IRegisterable, Comparable<StatusEffect>
{
    private static Collection<PotionEffectType> MAX_DURATION_EFFECTS = Arrays.asList(PotionEffectType.BLINDNESS, PotionEffectType.CONFUSION, PotionEffectType.NIGHT_VISION);

    String id;
    double duration;
    LivingEntity entity;
    BukkitTask task;

    boolean active;
    private List<PotionEffectType> potionEffects = new ArrayList<>();
    private List<PotionEffect> existingPotionEffects = new ArrayList<>();

    public StatusEffect()
    {
        this(0);
    }

    public StatusEffect(double duration)
    {
        this.setDuration(duration);
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

        Bukkit.getPluginManager().registerEvents(this, Bolster.getInstance());

        for (PotionEffect effect : entity.getActivePotionEffects())
        {
            entity.sendMessage("Effect " + effect.toString());
        }

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
            if (MAX_DURATION_EFFECTS.contains(potionEffect))
            {
                this.getEntity().removePotionEffect(potionEffect);
            }
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

    public void setId(String id)
    {
        this.id = id;
    }

    @Override
    public String getId()
    {
        return this.id != null ? this.id : Registries.STATUS_EFFECTS.getId(this);
    }

    @Override
    public String getDescription()
    {
        return this.getName();
    }

    public void addPotionEffect(PotionEffectType type, int amplifier, boolean ambient, boolean particles, boolean icon)
    {
        this.addPotionEffect(type, (int) (this.getDuration() * 20), amplifier, ambient, particles, icon);
    }

    public void addPotionEffect(PotionEffectType type, int duration, int amplifier, boolean ambient, boolean particles, boolean icon)
    {
        this.potionEffects.add(type);

        if (MAX_DURATION_EFFECTS.contains(type)) duration = Integer.MAX_VALUE;

        this.existingPotionEffects.addAll(this.getEntity().getActivePotionEffects());

        this.getEntity().addPotionEffect(new PotionEffect(type, duration, amplifier, ambient, particles, icon));
    }

    public Collection<PotionEffectType> getPotionEffects()
    {
        return this.potionEffects;
    }

    public abstract void onStart();

    public abstract void onEnd();

    public abstract void onTick();

    @Override
    public int compareTo(StatusEffect o)
    {
        return 0;
    }
}
