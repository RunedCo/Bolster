package co.runed.bolster.abilities.listeners;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.entity.BolsterLivingEntity;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Event that triggers casting an ability when a block is broken
 */
public class PlayerBreakBlockListener implements Listener
{
    @EventHandler
    private void onPlayerBreakBlock(BlockBreakEvent event)
    {
        Player player = event.getPlayer();
        ItemStack stack = player.getInventory().getItemInMainHand();

        Properties properties = new Properties();
        properties.set(AbilityProperties.CASTER, BolsterLivingEntity.from(player));
        properties.set(AbilityProperties.WORLD, player.getWorld());
        properties.set(AbilityProperties.ITEM_STACK, stack);
        properties.set(AbilityProperties.BLOCK, event.getBlock());
        properties.set(AbilityProperties.EVENT, event);

        Bolster.getAbilityManager().trigger(player, AbilityTrigger.ON_BREAK_BLOCK, properties);
    }
}
