package co.runed.bolster.abilities.listeners;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.managers.AbilityManager;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Event that triggers casting an ability on throwing an egg
 */
public class PlayerThrowEggListener implements Listener
{
    @EventHandler
    private void onPlayerThrowEgg(PlayerEggThrowEvent event)
    {
        LivingEntity entity = event.getPlayer();
        ItemStack stack = event.getEgg().getItem();

        Properties properties = new Properties();
        properties.set(AbilityProperties.ITEM_STACK, stack);
        properties.set(AbilityProperties.FORCE, 1.0f);
        properties.set(AbilityProperties.VELOCITY, event.getEgg().getVelocity());
        properties.set(AbilityProperties.EVENT, event);

        AbilityManager.getInstance().trigger(entity, AbilityTrigger.ON_SHOOT, properties);
    }
}
