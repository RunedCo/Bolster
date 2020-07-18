package co.runed.bolster.abilities.listeners;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.properties.Properties;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;

public class EntitySpawnListener implements Listener
{
    @EventHandler
    private void onEntitySpawn(EntitySpawnEvent event)
    {
        if (!(event.getEntity() instanceof LivingEntity)) return;

        LivingEntity entity = (LivingEntity) event.getEntity();

        if(!Bolster.getAbilityManager().hasAbilities(entity, AbilityTrigger.ON_SPAWN)) return;

        ItemStack stack = entity.getEquipment().getItemInMainHand();

        Properties properties = new Properties();
        properties.set(AbilityProperties.CASTER, entity);
        properties.set(AbilityProperties.WORLD, entity.getWorld());
        properties.set(AbilityProperties.ITEM_STACK, stack);
        properties.set(AbilityProperties.EVENT, event);

        Bolster.getAbilityManager().trigger(entity, AbilityTrigger.ON_SPAWN, properties);
    }
}
