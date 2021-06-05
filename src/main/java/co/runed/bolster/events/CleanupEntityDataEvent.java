package co.runed.bolster.events;

import co.runed.bolster.game.PlayerData;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class CleanupEntityDataEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();

    LivingEntity entity;
    boolean forced;

    public CleanupEntityDataEvent(LivingEntity entity, boolean forced)
    {
        super();

        this.entity = entity;
        this.forced = forced;
    }

    public LivingEntity getEntity()
    {
        return entity;
    }

    public boolean isForced()
    {
        return forced;
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
