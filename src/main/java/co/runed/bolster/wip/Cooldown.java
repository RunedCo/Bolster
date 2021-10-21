package co.runed.bolster.wip;

import co.runed.bolster.util.TimeUtil;
import co.runed.bolster.util.cooldown.CooldownSource;
import co.runed.dayroom.util.Identifiable;
import org.bukkit.entity.LivingEntity;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.UUID;

public class Cooldown implements Identifiable {
    private String id;
    private UUID owner;
    private CooldownSource source;
    private int slot;
    private boolean global = false;
    private ZonedDateTime startTime;
    private ZonedDateTime endTime;

    public Cooldown withId(String id) {
        this.id = id;

        return this;
    }

    public Cooldown withSlot(int slot) {
        this.slot = slot;

        return this;
    }

    public Cooldown withOwner(LivingEntity entity) {
        owner = entity.getUniqueId();

        return this;
    }

    public Cooldown withOwner(UUID uuid) {
        owner = uuid;

        return this;
    }

    public Cooldown setGlobal(boolean global) {
        this.global = global;

        return this;
    }

    public Cooldown until(ZonedDateTime until) {
        this.startTime = TimeUtil.now();
        this.endTime = until;

        return this;
    }

    public Cooldown ofDuration(Duration duration) {
        return until(TimeUtil.now().plus(duration));
    }

    public double getRemainingTime() {
        var sinceStart = Duration.between(TimeUtil.now(), endTime);

        return sinceStart.toMillis() / 1000d;
    }

    public boolean isDone() {
        return getRemainingTime() <= 0;
    }

    public boolean isGlobal() {
        return global;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public ZonedDateTime getEndTime() {
        return endTime;
    }

    public UUID getOwner() {
        return owner;
    }

    @Override
    public String getId() {
        return id;
    }

    public int getSlot() {
        return slot;
    }
}
