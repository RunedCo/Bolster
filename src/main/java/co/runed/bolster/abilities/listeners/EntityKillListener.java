package co.runed.bolster.abilities.listeners;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.properties.Properties;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Event that triggers casting an ability when an entity kills an entity
 */
public class EntityKillListener implements Listener
{
    @EventHandler
    private void onKillEntity(EntityDeathEvent event)
    {
        Player player = event.getEntity().getKiller();
        ItemStack stack = player.getInventory().getItemInMainHand();

        Properties properties = new Properties();
        properties.set(AbilityProperties.CASTER, player);
        properties.set(AbilityProperties.WORLD, player.getWorld());
        properties.set(AbilityProperties.ITEM_STACK, stack);
        properties.set(AbilityProperties.EVENT, event);
        properties.set(AbilityProperties.TARGETS, new ArrayList<>(Collections.singletonList(event.getEntity())));
        properties.set(AbilityProperties.DROPS, event.getDrops());

        Bolster.getAbilityManager().trigger(player, AbilityTrigger.ON_KILL_ENTITY, properties);
    }
}
