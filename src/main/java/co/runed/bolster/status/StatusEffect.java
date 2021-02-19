package co.runed.bolster.status;

import co.runed.bolster.Bolster;
import co.runed.bolster.managers.StatusEffectManager;
import co.runed.bolster.util.TaskUtil;
import co.runed.bolster.util.TimeUtil;
import co.runed.bolster.util.registries.IRegisterable;
import co.runed.bolster.util.registries.Registries;
import co.runed.bolster.wip.PotionUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.time.Instant;
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
    Instant startTime;
    double startingDuration;

    boolean active;
    private List<PotionData> potionEffects = new ArrayList<>();

    int changeNumber = 0;

    public StatusEffect()
    {
        this(0);
    }

    public StatusEffect(double duration)
    {
        this.startingDuration = duration;
        this.duration = duration;
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

    public void addDuration(double duration)
    {
        this.setDuration(this.getDuration() + duration);
    }

    public void setDuration(double duration)
    {
        this.duration = duration;

        long difference = (long) ((duration - this.startingDuration) * 20);
        long sinceStart = TimeUtil.toTicks(Duration.between(startTime, Instant.now()));

        for (PotionData data : this.potionEffects)
        {
            PotionUtil.PotionEffectContainer container = data.container;
            long finalDuration = (data.initialDuration - sinceStart) + difference;

            PotionUtil.removeExactPotionEffect(entity, container);

            data.container = PotionUtil.addPotionEffect(entity, new PotionEffect(container.type, (int) finalDuration, container.amplifier, container.ambient, container.particles, container.icon));
        }
    }

    public Duration getRemainingDuration()
    {
        Duration sinceStart = Duration.between(startTime, Instant.now());
        Duration remaining = TimeUtil.fromSeconds(this.getDuration()).minus(sinceStart);
        return remaining.isNegative() ? Duration.ZERO : remaining;
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

        this.startTime = Instant.now();

        if (this.canStart())
        {
            this.onStart();
        }

        this.createTask();
    }

    private void createTask()
    {
        this.task = TaskUtil.runDurationTaskTimer(Bolster.getInstance(), this::onTick, this.getRemainingDuration(), 0, 1L, this::end);
    }

    public void end()
    {
        if (!this.getRemainingDuration().isZero())
        {
            this.createTask();
            return;
        }

        for (PotionData potionEffect : this.potionEffects)
        {
            PotionUtil.removeExactPotionEffect(entity, potionEffect.container);
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
        this.addPotionEffect(type, (int) TimeUtil.toTicks(this.getRemainingDuration()), amplifier, ambient, particles, icon);
    }

    public void addPotionEffect(PotionEffectType type, int duration, int amplifier, boolean ambient, boolean particles, boolean icon)
    {
        if (MAX_DURATION_EFFECTS.contains(type)) duration = Integer.MAX_VALUE;

        PotionEffect effect = new PotionEffect(type, duration, amplifier, ambient, particles, icon);

        this.potionEffects.add(new PotionData(PotionUtil.addPotionEffect(entity, effect), duration));
    }

    public abstract void onStart();

    public abstract void onEnd();

    public abstract void onTick();

    @Override
    public int compareTo(StatusEffect o)
    {
        return 0;
    }

    private static class PotionData
    {
        PotionUtil.PotionEffectContainer container;
        long initialDuration;

        public PotionData(PotionUtil.PotionEffectContainer container, long initialDuration)
        {
            this.container = container;
            this.initialDuration = initialDuration;
        }
    }
}
