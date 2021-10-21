package co.runed.bolster.events.game;

import co.runed.bolster.game.GameMode;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class GameModeStartEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    GameMode gameMode;

    public GameModeStartEvent(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
