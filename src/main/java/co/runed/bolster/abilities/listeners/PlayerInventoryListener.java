package co.runed.bolster.abilities.listeners;

import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.managers.AbilityManager;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInventoryListener implements Listener
{
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerClickInventory(InventoryClickEvent event)
    {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (event.getClickedInventory() == null) return;

        Player player = (Player) event.getWhoClicked();
        ItemStack stack = event.getClickedInventory().getItem(event.getSlot());

        if (stack != null && ChatColor.stripColor(stack.getItemMeta().getDisplayName()).equals(""))
        {
            event.setResult(Event.Result.DENY);
        }

        Properties properties = new Properties();
        properties.set(AbilityProperties.ITEM_STACK, stack);
        properties.set(AbilityProperties.CURRENT_ITEM_STACK, event.getCurrentItem());
        properties.set(AbilityProperties.SLOT, event.getSlot());
        properties.set(AbilityProperties.RAW_SLOT, event.getRawSlot());
        properties.set(AbilityProperties.SLOT_TYPE, event.getSlotType());
        properties.set(AbilityProperties.CLICK_TYPE, event.getClick());
        properties.set(AbilityProperties.INVENTORY_ACTION, event.getAction());
        properties.set(AbilityProperties.INVENTORY, event.getClickedInventory());
        properties.set(AbilityProperties.EVENT, event);

        AbilityManager.getInstance().trigger(player, AbilityTrigger.ON_CLICK_INVENTORY, properties);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerOpenInventory(InventoryOpenEvent event)
    {
        Player player = (Player) event.getPlayer();

        Properties properties = new Properties();
        properties.set(AbilityProperties.INVENTORY, event.getInventory());
        properties.set(AbilityProperties.EVENT, event);

        AbilityManager.getInstance().trigger(player, AbilityTrigger.ON_OPEN_INVENTORY, properties);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerCloseInventory(InventoryCloseEvent event)
    {
        Player player = (Player) event.getPlayer();

        Properties properties = new Properties();
        properties.set(AbilityProperties.INVENTORY, event.getInventory());
        properties.set(AbilityProperties.EVENT, event);

        AbilityManager.getInstance().trigger(player, AbilityTrigger.ON_CLOSE_INVENTORY, properties);
    }
}
