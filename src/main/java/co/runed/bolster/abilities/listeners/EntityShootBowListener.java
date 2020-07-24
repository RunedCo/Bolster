package co.runed.bolster.abilities.listeners;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.properties.Properties;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Event that triggers casting ability on shooting a bow
 */
public class EntityShootBowListener implements Listener
{
    @EventHandler
    private void onLivingEntityShootBow(EntityShootBowEvent event)
    {
        LivingEntity entity = event.getEntity();
        ItemStack stack = event.getBow();

        Properties properties = new Properties();
        properties.set(AbilityProperties.EVENT, event);
        properties.set(AbilityProperties.CASTER, entity);
        properties.set(AbilityProperties.WORLD, entity.getWorld());
        properties.set(AbilityProperties.ITEM_STACK, stack);
        properties.set(AbilityProperties.FORCE, event.getForce());
        properties.set(AbilityProperties.VELOCITY, event.getProjectile().getVelocity());

        Bolster.getAbilityManager().trigger(entity, AbilityTrigger.ON_SHOOT, properties);
    }
}
