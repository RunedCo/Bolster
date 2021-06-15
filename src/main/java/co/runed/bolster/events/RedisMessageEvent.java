package co.runed.bolster.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RedisMessageEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();

    String channel;
    String message;

    public RedisMessageEvent(String channel, String message)
    {
        this.channel = channel;
        this.message = message;
    }

    public String getChannel()
    {
        return channel;
    }

    public String getMessage()
    {
        return message;
    }

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
