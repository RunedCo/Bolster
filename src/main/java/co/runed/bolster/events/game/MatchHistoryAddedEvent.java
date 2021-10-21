package co.runed.bolster.events.game;

import co.runed.bolster.match.MatchHistory;
import co.runed.bolster.match.MatchHistoryEvent;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MatchHistoryAddedEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    boolean cancelled;

    MatchHistory matchHistory;
    MatchHistoryEvent event;

    public MatchHistoryAddedEvent(MatchHistory matchHistory, MatchHistoryEvent event) {
        this.matchHistory = matchHistory;
        this.event = event;
    }

    public MatchHistory getMatchHistory() {
        return matchHistory;
    }

    public MatchHistoryEvent getEvent() {
        return event;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}