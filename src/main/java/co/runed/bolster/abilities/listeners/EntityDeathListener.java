package co.runed.bolster.abilities.listeners;

import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.managers.AbilityManager;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class EntityDeathListener implements Listener
{
    @EventHandler
    private void onEntityDeath(EntityDeathEvent event)
    {
        LivingEntity entity = event.getEntity();
        EntityEquipment inv = entity.getEquipment();
        ItemStack stack = inv.getItemInMainHand();

        Properties properties = new Properties();
        properties.set(AbilityProperties.ITEM_STACK, stack);
        properties.set(AbilityProperties.EVENT, event);

        AbilityManager.getInstance().trigger(entity, AbilityTrigger.ON_DEATH, properties);
    }
}
