package co.runed.bolster.events;

import co.runed.bolster.abilities.AbilityTrigger;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class EntityPreCastAbilityEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private final LivingEntity entity;
    private final AbilityTrigger trigger;

    public EntityPreCastAbilityEvent(LivingEntity entity, AbilityTrigger trigger)
    {
        this.entity = entity;
        this.trigger = trigger;
    }

    public LivingEntity getEntity()
    {
        return entity;
    }

    public AbilityTrigger getTrigger()
    {
        return trigger;
    }

    @Override
    public boolean isCancelled()
    {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled)
    {
        this.cancelled = cancelled;
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
}
