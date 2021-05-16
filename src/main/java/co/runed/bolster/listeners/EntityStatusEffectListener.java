package co.runed.bolster.listeners;

import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.events.EntityAddStatusEffectEvent;
import co.runed.bolster.events.EntityRemoveStatusEffectEvent;
import co.runed.bolster.managers.AbilityManager;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class EntityStatusEffectListener implements Listener
{
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onEntityStatusEffectRemoved(EntityRemoveStatusEffectEvent event)
    {
        LivingEntity entity = event.getEntity();

        Properties properties = new Properties();
        properties.set(AbilityProperties.STATUS_EFFECT, event.getStatusEffect());
        properties.set(AbilityProperties.STATUS_EFFECT_REMOVAL_CAUSE, event.getCause());
        properties.set(AbilityProperties.EVENT, event);

        AbilityManager.getInstance().trigger(entity, AbilityTrigger.ON_REMOVE_STATUS_EFFECT, properties);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onEntityStatusEffectAdded(EntityAddStatusEffectEvent event)
    {
        LivingEntity entity = event.getEntity();

        Properties properties = new Properties();
        properties.set(AbilityProperties.STATUS_EFFECT, event.getStatusEffect());
        properties.set(AbilityProperties.EVENT, event);

        AbilityManager.getInstance().trigger(entity, AbilityTrigger.ON_ADD_STATUS_EFFECT, properties);
    }
}