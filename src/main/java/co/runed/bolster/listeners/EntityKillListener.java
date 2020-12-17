package co.runed.bolster.listeners;

import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.managers.AbilityManager;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Event that triggers casting an ability when an entity kills an entity
 */
public class EntityKillListener implements Listener
{
    @EventHandler
    private void onKillEntity(EntityDeathEvent event)
    {
        Player player = event.getEntity().getKiller();

        if (player == null) return;

        ItemStack stack = player.getInventory().getItemInMainHand();

        Properties properties = new Properties();
        properties.set(AbilityProperties.ITEM_STACK, stack);
        properties.set(AbilityProperties.EVENT, event);
        properties.set(AbilityProperties.TARGET, event.getEntity());
        properties.set(AbilityProperties.DROPS, event.getDrops());

        AbilityManager.getInstance().trigger(player, AbilityTrigger.ON_KILL_ENTITY, properties);
    }
}