package co.runed.bolster.abilities.listeners;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.properties.Properties;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Event that triggers casting an ability on sneak
 */
public class PlayerSneakListener implements Listener
{
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerSneak(PlayerToggleSneakEvent event)
    {
        if (!event.isSneaking()) return;

        Player player = event.getPlayer();
        ItemStack stack = player.getInventory().getItemInMainHand();

        Properties properties = new Properties();
        properties.set(AbilityProperties.EVENT, event);
        properties.set(AbilityProperties.CASTER, player);
        properties.set(AbilityProperties.WORLD, player.getWorld());
        properties.set(AbilityProperties.ITEM_STACK, stack);

        Bolster.getAbilityManager().trigger(player, AbilityTrigger.ON_SNEAK, properties);
    }
}
