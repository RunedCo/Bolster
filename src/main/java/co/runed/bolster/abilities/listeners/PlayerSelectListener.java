package co.runed.bolster.abilities.listeners;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.entity.BolsterLivingEntity;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerSelectListener implements Listener
{
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSelectItem(PlayerItemHeldEvent event)
    {
        Player player = event.getPlayer();
        PlayerInventory inv = player.getInventory();

        ItemStack newItem = inv.getItem(event.getNewSlot());

        Properties properties = new Properties();
        properties.set(AbilityProperties.EVENT, event);
        properties.set(AbilityProperties.CASTER, BolsterLivingEntity.from(player));
        properties.set(AbilityProperties.WORLD, player.getWorld());
        properties.set(AbilityProperties.ITEM_STACK, newItem);

        Bolster.getAbilityManager().trigger(player, AbilityTrigger.ON_SELECT_ITEM, properties);

        ItemStack previousItem = inv.getItem(event.getPreviousSlot());
        Properties deselectProperties = new Properties(properties);
        deselectProperties.set(AbilityProperties.ITEM_STACK, previousItem);

        Bolster.getAbilityManager().trigger(player, AbilityTrigger.ON_DESELECT_ITEM, deselectProperties);
    }
}
