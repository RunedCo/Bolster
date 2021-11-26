package co.runed.bolster.status;

import co.runed.bolster.Bolster;
import co.runed.bolster.events.entity.EntityAddStatusEffectEvent;
import co.runed.bolster.events.entity.EntityRemoveStatusEffectEvent;
import co.runed.bolster.managers.StatusEffectManager;
import co.runed.bolster.util.BukkitUtil;
import co.runed.bolster.util.TimeUtil;
import co.runed.bolster.util.lang.Lang;
import co.runed.bolster.util.registries.Registries;
import co.runed.bolster.util.task.TaskUtil;
import co.runed.bolster.wip.PotionSystem;
import co.runed.dayroom.util.Describable;
import co.runed.dayroom.util.Identifiable;
import co.runed.dayroom.util.Nameable;
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

public abstract class StatusEffect implements Listener, Identifiable, Nameable, Describable, Comparable<StatusEffect> {
    private static final Collection<PotionEffectType> MAX_DURATION_EFFECTS = Arrays.asList(PotionEffectType.BLINDNESS, PotionEffectType.CONFUSION, PotionEffectType.NIGHT_VISION);
    private final List<StatusEffectPotionData> potionEffects = new ArrayList<>();
    String id;
    double duration;
    LivingEntity entity;
    BukkitTask task;
    Instant startTime;
    double startingDuration;
    boolean ambient = false;
    boolean active;
    private boolean cleared = false;

    public StatusEffect() {
        this(0);
    }

    public StatusEffect(double duration) {
        this.startingDuration = duration;
        this.duration = duration;
    }

    public String getName() {
        return Lang.str("status." + getId() + ".name", "status.default.name");
    }

    public boolean isNegative() {
        return false;
    }

    public boolean isAmbient() {
        return ambient;
    }

    public StatusEffect setAmbient(boolean ambient) {
        this.ambient = ambient;

        return this;
    }

    public ChatColor getColor() {
        return ChatColor.WHITE;
    }

    public Collection<Class<? extends StatusEffect>> getOverrides() {
        return new ArrayList<>();
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;

        var difference = (long) ((duration - this.startingDuration) * 20);
        var sinceStart = TimeUtil.toTicks(Duration.between(startTime, Instant.now()));

        for (var data : this.potionEffects) {
            var container = data.container;
            var finalDuration = (data.initialDuration - sinceStart) + difference;

            PotionSystem.removeExactPotionEffect(entity, container);

            data.container = PotionSystem.addPotionEffect(entity, new PotionEffect(container.type, (int) finalDuration, container.amplifier, container.ambient, container.particles, container.icon));
        }
    }

    public void addDuration(double duration) {
        this.setDuration(this.getDuration() + duration);
    }

    public Duration getRemainingDuration() {
        if (this.duration >= Integer.MAX_VALUE) return TimeUtil.fromSeconds(this.duration);

        var sinceStart = Duration.between(startTime, Instant.now());
        var remaining = TimeUtil.fromSeconds(this.getDuration()).minus(sinceStart);
        return remaining.isNegative() ? Duration.ZERO : remaining;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public void setEntity(LivingEntity entity) {
        this.entity = entity;
    }

    public boolean canStart() {
        return true;
    }

    public boolean isActive() {
        return this.active;
    }

    public void start(LivingEntity entity) {
        this.setEntity(entity);

        this.startTime = Instant.now();

        var event = BukkitUtil.triggerEvent(new EntityAddStatusEffectEvent(entity, this));

        if (event.isCancelled()) {
            this.clear(RemovalCause.CANCELLED);
            return;
        }

        Bukkit.getPluginManager().registerEvents(this, Bolster.getInstance());

        if (this.canStart()) {
            this.onStart();
        }

        this.createTask();
    }

    private void createTask() {
        this.task = TaskUtil.runDurationTaskTimer(Bolster.getInstance(), this::onTick, this.getRemainingDuration(), 0, 1L, this::end);
    }

    public void end() {
        if (!this.getRemainingDuration().isZero()) {
            this.createTask();
            return;
        }

        if (!cleared) {
            BukkitUtil.triggerEvent(new EntityRemoveStatusEffectEvent(entity, this, RemovalCause.EXPIRED, null));
        }

        for (var potionEffect : this.potionEffects) {
            PotionSystem.removeExactPotionEffect(entity, potionEffect.container);
        }

        StatusEffectManager.getInstance().removeStatusEffect(this.getEntity(), this);

        HandlerList.unregisterAll(this);

        this.onEnd();
    }

    public void clear() {
        this.clear(false);
    }

    public void clear(boolean forced) {
        this.clear(forced ? RemovalCause.FORCE_CLEARED : RemovalCause.CLEARED);
    }

    protected void clear(RemovalCause cause) {
        this.clear(cause, null);
    }

    // NOTE: Data object is a bit of a janky workaround, maybe find another solution here?
    protected void clear(RemovalCause cause, Object data) {
        if (this.task == null || this.task.isCancelled()) return;

        var forced = cause == RemovalCause.FORCE_CLEARED;

        var event = BukkitUtil.triggerEvent(new EntityRemoveStatusEffectEvent(entity, this, cause, data));

        if (event.isCancelled() && !forced) return;

        this.cleared = true;

        this.duration = 0;

        this.task.cancel();
        this.end();
    }

    @Override
    public String getId() {
        return this.id != null ? this.id : Registries.STATUS_EFFECTS.getId(this);
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getDescription() {
        return this.getName();
    }

    public void addPotionEffect(PotionEffectType type, int amplifier, boolean ambient, boolean particles, boolean icon) {
        this.addPotionEffect(type, (int) TimeUtil.toTicks(this.getRemainingDuration()), amplifier, ambient, particles, icon);
    }

    public void addPotionEffect(PotionEffectType type, int duration, int amplifier, boolean ambient, boolean particles, boolean icon) {
        if (MAX_DURATION_EFFECTS.contains(type)) duration = Integer.MAX_VALUE;

        var effect = new PotionEffect(type, duration, amplifier, ambient, particles, icon);

        this.potionEffects.add(new StatusEffectPotionData(PotionSystem.addPotionEffect(entity, effect), duration));
    }

    public abstract void onStart();

    public abstract void onEnd();

    public abstract void onTick();

    @Override
    public int compareTo(StatusEffect o) {
        return 0;
    }

    public enum RemovalCause {
        CLEARED, // cleared with .clear() method
        EXPIRED, // timed out
        CANCELLED, // cancelled when adding
        FORCE_CLEARED, // force cleared
        INTERNAL // internal status effect (e.g stealth effect cancelling on damage taken or dealt)
    }

    private static class StatusEffectPotionData {
        PotionSystem.PotionEffectContainer container;
        long initialDuration;

        public StatusEffectPotionData(PotionSystem.PotionEffectContainer container, long initialDuration) {
            this.container = container;
            this.initialDuration = initialDuration;
        }
    }
}
