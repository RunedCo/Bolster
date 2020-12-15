package co.runed.bolster.listeners;

import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.managers.AbilityManager;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectListener implements Listener
{
    @EventHandler
    private void onPlayerConnect(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();

        Properties properties = new Properties();
        properties.set(AbilityProperties.EVENT, event);

        AbilityManager.getInstance().trigger(player, AbilityTrigger.ON_CONNECT, properties);
    }

    @EventHandler
    private void onPlayerDisconnect(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();

        Properties properties = new Properties();
        properties.set(AbilityProperties.EVENT, event);

        AbilityManager.getInstance().trigger(player, AbilityTrigger.ON_DISCONNECT, properties);
    }
}
