package co.runed.bolster.managers;

import co.runed.bolster.sidebar.Sidebar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScoreboardManager {
    Map<Player, Scoreboard> playerScoreboards = new HashMap<>();
    Map<Player, Sidebar> playerSidebars = new HashMap<>();

    public ScoreboardManager(Plugin plugin) {

    }

    /**
     * Gets the player's scoreboard instance
     *
     * @param player The player
     * @return The scoreboard
     */
    public Scoreboard getScoreboard(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        if(playerScoreboards.containsKey(player)) {
            scoreboard = playerScoreboards.get(player);
        }

        player.setScoreboard(scoreboard);

        playerScoreboards.put(player, scoreboard);

        return scoreboard;
    }

    public void setScoreboard(Player player, Scoreboard scoreboard) {
        this.removeSidebar(player);

        player.setScoreboard(scoreboard);

        playerScoreboards.put(player, scoreboard);
    }

    public void clearScoreboard(Player player) {
        this.removeSidebar(player);

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        player.setScoreboard(scoreboard);

        playerScoreboards.put(player, scoreboard);
    }

    /**
     * Gets the player's active sidebar instance
     *
     * @param player The player
     * @return The sidebar
     */
    public Sidebar getSidebar(Player player) {
        if(!this.playerSidebars.containsKey(player)) return null;

        return this.playerSidebars.get(player);
    }

    public void setSidebar(Player player, Sidebar sidebar) {
        if(this.playerSidebars.containsKey(player)) {
            this.playerSidebars.get(player).removePlayer(player);
        }

        sidebar.addPlayer(player);

        this.playerSidebars.put(player, sidebar);
    }

    public void removeSidebar(Player player) {
        if(this.playerSidebars.containsKey(player)) {
            this.playerSidebars.get(player).removePlayer(player);
        }

        this.playerSidebars.remove(player);
    }
}
