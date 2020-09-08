package co.runed.bolster.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class PlayerManager implements Listener
{
    Map<UUID, Player> players = new HashMap<>();

    public PlayerManager(Plugin plugin)
    {
    }

    public Player getPlayerByName(String name)
    {
        Player player = Bukkit.getPlayer(name);

        for (Player p : this.players.values())
        {
            if (p.equals(player)) return p;
        }

        return null;
    }

    public Player getPlayerByUUID(UUID uuid)
    {
        if (this.players.containsKey(uuid))
        {
            return this.players.get(uuid);
        }

        return null;
    }

    public Collection<Player> getAllPlayers()
    {
        return this.players.values();
    }

    public Collection<Player> getOnlinePlayers()
    {
        Collection<Player> players = new ArrayList<>();

        for (Player player : this.getAllPlayers())
        {
            if (player.isOnline())
            {
                players.add(player);
            }
        }

        return players;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerConnect(PlayerJoinEvent event)
    {

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDisconnect(PlayerQuitEvent event)
    {

    }
}
