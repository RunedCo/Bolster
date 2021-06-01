package co.runed.bolster.wip;

import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

import java.util.*;

public class CombatTracker implements Listener
{
    private static Map<UUID, LivingEntity> damageMap = new HashMap<>();
    private static Map<UUID, List<Mob>> targetedByMap = new HashMap<>();

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

    public static void clearAggro(LivingEntity entity)
    {
        UUID uuid = entity.getUniqueId();

        if (!targetedByMap.containsKey(uuid)) return;

        List<Mob> targeters = targetedByMap.get(uuid);

        for (Mob target : targeters)
        {
            if (target.getTarget().getUniqueId().equals(uuid))
            {
                target.setTarget(null);
            }
        }

        targeters.remove(uuid);
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onEntityTarget(EntityTargetLivingEntityEvent event)
    {
        EntityTargetEvent.TargetReason reason = event.getReason();
        LivingEntity target = event.getTarget();
        Mob entity = (Mob) event.getEntity();

        if (target == null)
        {

        }

        UUID uuid = target.getUniqueId();
        if (!targetedByMap.containsKey(uuid)) targetedByMap.put(uuid, new ArrayList<>());
    }
}
