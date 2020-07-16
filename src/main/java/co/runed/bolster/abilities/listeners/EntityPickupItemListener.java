package co.runed.bolster.abilities.listeners;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.abilities.properties.AbilityProperties;
import co.runed.bolster.properties.Properties;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Event that triggers casting an ability when an item is picked up
 */
public class EntityPickupItemListener implements Listener
{
    @EventHandler
    private void onPickupItem(EntityPickupItemEvent event)
    {
        LivingEntity entity = event.getEntity();
        ItemStack stack = event.getItem().getItemStack();

        Properties properties = new Properties();
        properties.set(AbilityProperties.EVENT, event);
        properties.set(AbilityProperties.CASTER, entity);
        properties.set(AbilityProperties.ITEM_STACK, stack);

        Bolster.getAbilityManager().trigger(entity, AbilityTrigger.ON_PICKUP_ITEM, properties);
    }
}
