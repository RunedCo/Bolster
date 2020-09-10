package co.runed.bolster.abilities.listeners;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.managers.AbilityManager;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class EntitySpawnListener implements Listener
{
    @EventHandler(priority = EventPriority.MONITOR)
    private void onEntitySpawn(EntitySpawnEvent event)
    {
        if (!(event.getEntity() instanceof LivingEntity)) return;

        this.run(event, (LivingEntity) event.getEntity());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerJoin(PlayerJoinEvent event)
    {
        this.run(event, event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerRespawn(PlayerRespawnEvent event)
    {
        this.run(event, event.getPlayer());
    }

    private void run(Event event, LivingEntity entity)
    {
        if (!AbilityManager.getInstance().hasAbilities(entity, AbilityTrigger.ON_SPAWN)) return;

        ItemStack stack = entity.getEquipment().getItemInMainHand();

        Properties properties = new Properties();
        properties.set(AbilityProperties.ITEM_STACK, stack);
        properties.set(AbilityProperties.EVENT, event);

        AbilityManager.getInstance().trigger(entity, AbilityTrigger.ON_SPAWN, properties);
    }
}
