package co.runed.bolster.events;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

public class EntityTargetedEvent extends EntityEvent implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled = false;
    private final Entity targeter;

    public EntityTargetedEvent(Entity what)
    {
        this(what, null);
    }

    public EntityTargetedEvent(Entity what, Entity targeter)
    {
        super(what);

        this.targeter = targeter;
    }

    public Entity getTargeter()
    {
        return targeter;
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
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean b)
    {
        this.cancelled = b;
    }
}
