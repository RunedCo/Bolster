package co.runed.bolster.events.game;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class GameModePauseChangeEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();

    boolean paused;

    public GameModePauseChangeEvent(boolean paused)
    {
        this.paused = paused;
    }

    public boolean isPaused()
    {
        return paused;
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
