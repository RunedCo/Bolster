package co.runed.bolster.wip;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HitTracker implements Listener
{
    private static Map<UUID, LivingEntity> damageMap = new HashMap<>();

    // returns the last entity hit by another entity, does not check that they did damage, reset on death
    public static LivingEntity getLastHit(LivingEntity damager)
    {
        if (!damageMap.containsKey(damager.getUniqueId())) return null;

        return damageMap.get(damager.getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onDamageEntity(EntityDamageByEntityEvent event)
    {
        Entity damager = event.getDamager();

        if (damager instanceof LivingEntity && event.getEntity() instanceof LivingEntity)
        {
            damageMap.put(damager.getUniqueId(), (LivingEntity) event.getEntity());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onEntityDie(EntityDeathEvent event)
    {
        damageMap.remove(event.getEntity().getUniqueId());
    }
}
