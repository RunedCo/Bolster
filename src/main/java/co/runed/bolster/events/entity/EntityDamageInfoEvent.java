package co.runed.bolster.events.entity;

import co.runed.bolster.damage.DamageInfo;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EntityDamageInfoEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private static final Function<? super Double, Double> ZERO = Functions.constant(-0.0);

    private Entity entity;
    private DamageInfo damageInfo;
    private EntityDamageEvent wrappedEvent = null;
    private boolean cancelled = false;

    public EntityDamageInfoEvent(@NotNull Entity entity, DamageInfo info, EntityDamageEvent event) {
        this.entity = entity;
        this.damageInfo = info;
        this.wrappedEvent = event;
    }

    @NotNull
    public Entity getEntity() {
        return entity;
    }

    @Nullable
    public Entity getDamager() {
        if (wrappedEvent instanceof EntityDamageByEntityEvent byEntityEvent) {
            return byEntityEvent.getDamager();
        }

        return null;
    }

    public EntityDamageEvent getWrappedEvent() {
        return wrappedEvent;
    }

    public DamageInfo getDamageInfo() {
        return damageInfo;
    }

    public double getDamage() {
        return wrappedEvent.getDamage();
    }

    public double getFinalDamage() {
        return wrappedEvent.getFinalDamage();
    }

    public void setDamage(double damage) {
        wrappedEvent.setDamage(damage);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
