package co.runed.bolster.abilities.listeners;

import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.managers.AbilityManager;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Event that triggers casting an ability when a block is broken
 */
public class PlayerBreakBlockListener implements Listener
{
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerBreakBlock(BlockBreakEvent event)
    {
        Player player = event.getPlayer();
        ItemStack stack = player.getInventory().getItemInMainHand();

        Properties properties = new Properties();
        properties.set(AbilityProperties.ITEM_STACK, stack);
        properties.set(AbilityProperties.BLOCK, event.getBlock());
        properties.set(AbilityProperties.EVENT, event);

        AbilityManager.getInstance().trigger(player, AbilityTrigger.ON_BREAK_BLOCK, properties);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerDamageBlock(BlockDamageEvent event)
    {
        Player player = event.getPlayer();
        ItemStack stack = event.getItemInHand();

        Properties properties = new Properties();
        properties.set(AbilityProperties.ITEM_STACK, stack);
        properties.set(AbilityProperties.BLOCK, event.getBlock());
        properties.set(AbilityProperties.EVENT, event);

        AbilityManager.getInstance().trigger(player, AbilityTrigger.ON_DAMAGE_BLOCK, properties);
    }
}
