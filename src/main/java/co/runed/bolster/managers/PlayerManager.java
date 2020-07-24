package co.runed.bolster.managers;

import co.runed.bolster.BolsterPlayer;
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
    Map<UUID, BolsterPlayer> players = new HashMap<>();

    public PlayerManager(Plugin plugin)
    {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public BolsterPlayer getPlayerByName(String name)
    {
        Player player = Bukkit.getPlayer(name);

        for (BolsterPlayer p : this.players.values())
        {
            if (p.getBukkitPlayer().equals(player)) return p;
        }

        return null;
    }

    public BolsterPlayer getPlayerByUUID(UUID uuid)
    {
        if (this.players.containsKey(uuid))
        {
            return this.players.get(uuid);
        }

        return null;
    }

    public Collection<BolsterPlayer> getAllPlayers()
    {
        return this.players.values();
    }

    public Collection<BolsterPlayer> getOnlinePlayers()
    {
        Collection<BolsterPlayer> players = new ArrayList<>();

        for (BolsterPlayer player : this.getAllPlayers())
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
