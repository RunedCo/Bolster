package co.runed.bolster.events.player;

import co.runed.bolster.game.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class SavePlayerDataEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();

    Player player;
    PlayerData playerData;

    public SavePlayerDataEvent(Player player, PlayerData playerData)
    {
        super();

        this.player = player;
        this.playerData = playerData;
    }

    public Player getPlayer()
    {
        return player;
    }

    public PlayerData getPlayerData()
    {
        return playerData;
    }

    public void setPlayerData(PlayerData playerData)
    {
        this.playerData = playerData;
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
