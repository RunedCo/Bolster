package co.runed.bolster.abilities.listeners;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.abilities.properties.AbilityProperties;
import co.runed.bolster.properties.Properties;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Event that triggers casting an ability on swapping offhand (pushing F)
 */
public class PlayerOffhandListener implements Listener
{
    @EventHandler
    private void onPlayerOffhand(PlayerSwapHandItemsEvent event)
    {
        Player player = event.getPlayer();
        ItemStack stack = event.getOffHandItem();

        Properties properties = new Properties();
        properties.set(AbilityProperties.CASTER, player);
        properties.set(AbilityProperties.WORLD, player.getWorld());
        properties.set(AbilityProperties.EVENT, event);
        properties.set(AbilityProperties.ITEM_STACK, stack);

        event.setCancelled(true);

        Bolster.getAbilityManager().trigger(player, AbilityTrigger.ON_SWAP_OFFHAND, properties);
    }
}
