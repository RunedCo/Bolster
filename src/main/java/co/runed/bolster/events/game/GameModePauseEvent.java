package co.runed.bolster.events.game;

import co.runed.bolster.game.GameMode;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class GameModePauseEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();

    GameMode gameMode;
    boolean paused;

    public GameModePauseEvent(GameMode gameMode, boolean paused)
    {
        this.gameMode = gameMode;
        this.paused = paused;
    }

    public GameMode getGameMode()
    {
        return gameMode;
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
