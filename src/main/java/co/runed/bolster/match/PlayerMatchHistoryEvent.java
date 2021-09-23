package co.runed.bolster.match;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.UUID;

public abstract class PlayerMatchHistoryEvent extends MatchHistoryEvent
{
    UUID uuid;
    Vector location;

    public PlayerMatchHistoryEvent(String id, Player player)
    {
        super(id);

        this.uuid = player.getUniqueId();
        this.location = player.getLocation().toVector();
    }

    public UUID getUuid()
    {
        return uuid;
    }

    public Vector getLocation()
    {
        return location;
    }

    public void setLocation(Vector location)
    {
        this.location = location;
    }
}
