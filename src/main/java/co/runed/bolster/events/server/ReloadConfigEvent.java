package co.runed.bolster.events.server;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ReloadConfigEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers()
    {
        return handlers;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}
