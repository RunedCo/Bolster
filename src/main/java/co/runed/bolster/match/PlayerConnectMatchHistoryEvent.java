package co.runed.bolster.match;

import org.bukkit.entity.Player;

public class PlayerConnectMatchHistoryEvent extends PlayerMatchHistoryEvent {
    public PlayerConnectMatchHistoryEvent(Player player) {
        super("player_connect", player);
    }
}
