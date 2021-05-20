package co.runed.bolster.events;

import co.runed.bolster.status.StatusEffect;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class EntityRemoveStatusEffectEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();

    private final LivingEntity entity;
    private final StatusEffect statusEffect;
    private final StatusEffect.RemovalCause cause;
    private boolean cancelled = false;

    public EntityRemoveStatusEffectEvent(LivingEntity entity, StatusEffect statusEffect, StatusEffect.RemovalCause cause)
    {
        this.entity = entity;
        this.statusEffect = statusEffect;
        this.cause = cause;
    }

    public LivingEntity getEntity()
    {
        return entity;
    }

    public StatusEffect getStatusEffect()
    {
        return statusEffect;
    }

    public StatusEffect.RemovalCause getCause()
    {
        return cause;
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }

    @Override
    public boolean isCancelled()
    {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b)
    {
        cancelled = b;
    }

}

