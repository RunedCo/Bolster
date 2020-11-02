package co.runed.bolster.abilities.listeners;

import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.managers.AbilityManager;
import co.runed.bolster.managers.ItemManager;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerSelectListener implements Listener
{

    private void trigger(Event event, LivingEntity entity, ItemStack newItem, ItemStack oldItem)
    {
        if(newItem != null)
        {
            Properties properties = new Properties();
            properties.set(AbilityProperties.EVENT, event);
            properties.set(AbilityProperties.ITEM_STACK, newItem);

            AbilityManager.getInstance().trigger(entity, AbilityTrigger.ON_SELECT_ITEM, properties);
        }

        if(oldItem != null)
        {
            Properties properties = new Properties();
            properties.set(AbilityProperties.EVENT, event);
            properties.set(AbilityProperties.ITEM_STACK, oldItem);

            AbilityManager.getInstance().trigger(entity, AbilityTrigger.ON_DESELECT_ITEM, properties);
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSelectItem(PlayerItemHeldEvent event)
    {
        Player player = event.getPlayer();

        PlayerInventory inv = player.getInventory();

        ItemStack newItem = inv.getItem(event.getNewSlot());
        ItemStack previousItem = inv.getItem(event.getPreviousSlot());

        this.trigger(event, player, newItem, previousItem);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryInteract(InventoryInteractEvent event)
    {

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDropItem(PlayerDropItemEvent event)
    {
        Player player = event.getPlayer();
        PlayerInventory inv = player.getInventory();

        ItemStack item = event.getItemDrop().getItemStack();
        String itemId = ItemManager.getInstance().getItemIdFromStack(item);

        if(itemId == null) return;
        if(!itemId.equals(ItemManager.getInstance().getItemIdFromStack(inv.getItemInMainHand()))) return;

        this.trigger(event, player, null, item);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPickupItem(EntityPickupItemEvent event)
    {
        if(event.getEntityType() != EntityType.PLAYER) return;

        Player player = (Player) event.getEntity();
        PlayerInventory inv = player.getInventory();

        ItemStack newItem = event.getItem().getItemStack();

        if(!inv.getItemInMainHand().isSimilar(newItem)) return;

        this.trigger(event, player, newItem, null);
    }
}
