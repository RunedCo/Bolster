package co.runed.bolster.managers;

import co.runed.bolster.gui.sidebar.Sidebar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class SidebarManager extends Manager
{
    Map<Player, Sidebar> playerSidebars = new HashMap<>();

    private static SidebarManager _instance;

    public SidebarManager(Plugin plugin)
    {
        super(plugin);

        _instance = this;
    }

    /**
     * Gets the player's active sidebar instance
     *
     * @param player the player
     * @return the sidebar
     */
    public Sidebar getSidebar(Player player)
    {
        if (!this.playerSidebars.containsKey(player)) return null;

        return this.playerSidebars.get(player);
    }

    /**
     * Set a player's active sidebar instance
     *
     * @param player  the player
     * @param sidebar the sidebar
     */
    public void setSidebar(Player player, Sidebar sidebar)
    {
        if (this.playerSidebars.containsKey(player))
        {
            this.playerSidebars.get(player).removePlayer(player);
        }

        sidebar.addPlayer(player);

        this.playerSidebars.put(player, sidebar);
    }

    /**
     * Clears a players active sidebar instance
     *
     * @param player the player
     */
    public void clearSidebar(Player player)
    {
        if (this.playerSidebars.containsKey(player))
        {
            this.playerSidebars.get(player).removePlayer(player);
        }

        this.playerSidebars.remove(player);
    }

    public static SidebarManager getInstance()
    {
        return _instance;
    }
}
