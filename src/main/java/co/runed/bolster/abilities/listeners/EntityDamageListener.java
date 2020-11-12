package co.runed.bolster.abilities.listeners;

import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.managers.AbilityManager;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

/**
 * Event that triggers casting an ability when an entity is damaged
 */
public class EntityDamageListener implements Listener
{
    @EventHandler
    private void onDamageEntity(EntityDamageByEntityEvent event)
    {
        if (!(event.getDamager() instanceof LivingEntity)) return;

        LivingEntity entity = (LivingEntity) event.getDamager();
        EntityEquipment inv = entity.getEquipment();
        ItemStack stack = inv.getItemInMainHand();

        Properties properties = new Properties();
        properties.set(AbilityProperties.TARGET, event.getEntity());
        properties.set(AbilityProperties.ITEM_STACK, stack);
        properties.set(AbilityProperties.EVENT, event);
        properties.set(AbilityProperties.DAMAGE, event.getFinalDamage());

        AbilityManager.getInstance().trigger(entity, AbilityTrigger.ON_DAMAGE_ENTITY, properties);
    }

    @EventHandler
    private void onLeftClickEntity(EntityDamageByEntityEvent event)
    {
        if (!(event.getDamager() instanceof Player)) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;

        Player player = (Player) event.getDamager();
        EntityEquipment inv = player.getEquipment();
        ItemStack stack = inv.getItemInMainHand();

        Properties properties = new Properties();
        properties.set(AbilityProperties.TARGET, event.getEntity());
        properties.set(AbilityProperties.ITEM_STACK, stack);
        properties.set(AbilityProperties.EVENT, event);
        properties.set(AbilityProperties.DAMAGE, event.getFinalDamage());

        AbilityManager.getInstance().trigger(player, AbilityTrigger.LEFT_CLICK_ENTITY, properties);
    }

    @EventHandler
    private void onEntityFatalDamage(EntityDamageEvent event)
    {
        if (!(event.getEntity() instanceof LivingEntity)) return;

        LivingEntity entity = (LivingEntity) event.getEntity();
        EntityEquipment inv = entity.getEquipment();
        ItemStack stack = inv.getItemInMainHand();
        double damage = event.getFinalDamage();

        Properties properties = new Properties();
        properties.set(AbilityProperties.EVENT, event);
        properties.set(AbilityProperties.ITEM_STACK, stack);
        properties.set(AbilityProperties.DAMAGE, damage);

        if (event instanceof EntityDamageByEntityEvent)
        {
            properties.set(AbilityProperties.DAMAGER, ((EntityDamageByEntityEvent) event).getDamager());
        }

        if (entity.getHealth() - damage <= 0)
        {
            AbilityManager.getInstance().trigger(entity, AbilityTrigger.ON_TAKE_FATAL_DAMAGE, properties);
        }

        AbilityManager.getInstance().trigger(entity, AbilityTrigger.ON_TAKE_DAMAGE, properties);
    }
}
