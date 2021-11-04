package co.runed.bolster.match;

import co.runed.bolster.game.Team;
import co.runed.bolster.managers.EntityManager;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class PlayerMatchHistoryEvent extends MatchHistoryEvent {
    private UUID playerUuid;
    private Vector playerLocation;
    private List<String> playerTeams = new ArrayList<>();

    public PlayerMatchHistoryEvent(String id, Player player) {
        super(id);

        this.playerUuid = player.getUniqueId();
        this.playerLocation = player.getLocation().toVector();
        this.playerTeams = EntityManager.getInstance().getJoinedTeams(player).stream().map(Team::getName).toList();
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public Vector getLocation() {
        return playerLocation;
    }

    public void setLocation(Vector playerLocation) {
        this.playerLocation = playerLocation;
    }
}
