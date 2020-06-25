package co.runed.bolster.abilities.listeners;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.abilities.properties.AbilityProperties;
import co.runed.bolster.items.Item;
import co.runed.bolster.properties.Properties;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Event that triggers casting an ability when an entity is damaged
 */
public class EntityDamageListener implements Listener {
    @EventHandler
    private void onDamageEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof LivingEntity)) return;

        LivingEntity entity = (LivingEntity) event.getDamager();
        EntityEquipment inv = entity.getEquipment();
        ItemStack stack = inv.getItemInMainHand();

        Properties properties = new Properties();
        properties.set(AbilityProperties.CASTER, entity);
        properties.set(AbilityProperties.TARGETS, new ArrayList<>(Collections.singletonList((LivingEntity) event.getEntity())));
        properties.set(AbilityProperties.ITEM_STACK, stack);
        properties.set(AbilityProperties.EVENT, event);
        properties.set(AbilityProperties.DAMAGE, event.getDamage());

        Bolster.getAbilityManager().trigger(entity, AbilityTrigger.ON_DAMAGE_ENTITY, properties);
    }
}
