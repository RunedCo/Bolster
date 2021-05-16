package co.runed.bolster.events;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.status.StatusEffect;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

public final class EntityRemoveStatusEffectEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();

    private final LivingEntity entity;
    private final StatusEffect statusEffect;
    private final Cause cause;
    private boolean cancelled = false;

    public EntityRemoveStatusEffectEvent(LivingEntity entity, StatusEffect statusEffect, Cause cause)
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

    public Cause getCause()
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

    public enum Cause
    {
        CLEARED, // cleared with .clear() method
        EXPIRED, // timed out
        CANCELLED, // cancelled when adding
        FORCE_CLEARED // force cleared
    }
}

