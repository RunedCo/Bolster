package co.runed.bolster.events;

import co.runed.bolster.status.StatusEffect;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class EntityAddStatusEffectEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();

    private final LivingEntity entity;
    private final StatusEffect statusEffect;
    private boolean cancelled = false;

    public EntityAddStatusEffectEvent(LivingEntity entity, StatusEffect statusEffect)
    {
        this.entity = entity;
        this.statusEffect = statusEffect;
    }

    public LivingEntity getEntity()
    {
        return entity;
    }

    public StatusEffect getStatusEffect()
    {
        return statusEffect;
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

