package co.runed.bolster.events.entity;

import co.runed.bolster.entity.BolsterEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class EntityDestroyEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();

    BolsterEntity entity;

    public EntityDestroyEvent(BolsterEntity entity)
    {
        super();

        this.entity = entity;
    }

    @NotNull
    public BolsterEntity getEntity()
    {
        return entity;
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
