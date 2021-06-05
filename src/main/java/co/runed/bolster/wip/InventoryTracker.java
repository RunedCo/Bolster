package co.runed.bolster.wip;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InventoryTracker implements Listener
{
    private static List<UUID> openInventories = new ArrayList<>();

    public static boolean isInventoryOpen(Player player)
    {
        return openInventories.contains(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlayerDie(PlayerDeathEvent event)
    {
        openInventories.remove(event.getEntity().getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlayerDisconnect(PlayerQuitEvent event)
    {
        openInventories.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onCloseInventory(InventoryCloseEvent event)
    {
        openInventories.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onOpenInventory(InventoryOpenEvent event)
    {
        openInventories.add(event.getPlayer().getUniqueId());
    }
}
