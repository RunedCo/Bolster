package co.runed.bolster.managers;

import co.runed.bolster.scoreboard.PacketScoreboard;
import co.runed.bolster.scoreboard.sidebar.Sidebar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import co.runed.bolster.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;

public class SidebarManager {
    Plugin plugin;

    Map<Player, Sidebar> playerSidebars = new HashMap<>();

    public SidebarManager(Plugin plugin) {
        this.plugin = plugin;
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
