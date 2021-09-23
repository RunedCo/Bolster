package co.runed.bolster.managers;

import co.runed.bolster.gui.sidebar.Sidebar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SidebarManager extends Manager {
    Map<UUID, Sidebar> playerSidebars = new HashMap<>();

    private static SidebarManager _instance;

    public SidebarManager(Plugin plugin) {
        super(plugin);

        _instance = this;
    }

    /**
     * Gets the player's active sidebar instance
     *
     * @param player the player
     * @return the sidebar
     */
    public Sidebar getSidebar(Player player) {
        if (!this.playerSidebars.containsKey(player.getUniqueId())) return null;

        return this.playerSidebars.get(player.getUniqueId());
    }

    /**
     * Set a player's active sidebar instance
     *
     * @param player  the player
     * @param sidebar the sidebar
     */
    public void setSidebar(Player player, Sidebar sidebar) {
        if (this.playerSidebars.containsKey(player.getUniqueId())) {
            this.playerSidebars.get(player.getUniqueId()).removePlayer(player);
        }

        sidebar.addPlayer(player);

        this.playerSidebars.put(player.getUniqueId(), sidebar);
    }

    /**
     * Clears a players active sidebar instance
     *
     * @param player the player
     */
    public void clearSidebar(Player player) {
        if (this.playerSidebars.containsKey(player.getUniqueId())) {
            this.playerSidebars.get(player.getUniqueId()).removePlayer(player);
        }

        this.playerSidebars.remove(player.getUniqueId());
    }

    public static SidebarManager getInstance() {
        return _instance;
    }
}
