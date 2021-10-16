package co.runed.bolster.wip;

import co.runed.bolster.events.entity.EntityCleanupEvent;
import co.runed.bolster.events.entity.EntityDamageInfoEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatSystem implements Listener {
    private static Map<UUID, LivingEntity> damageMap = new HashMap<>();
    //    private static Map<UUID, List<Mob>> targetedByMap = new HashMap<>();
    private static Map<Mob, LivingEntity> targetMap = new HashMap<>();

    // returns the last entity hit by another entity, does not check that they did damage, reset on death
    public static LivingEntity getLastHit(LivingEntity damager) {
        if (!damageMap.containsKey(damager.getUniqueId())) return null;

        return damageMap.get(damager.getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onDamageEntity(EntityDamageInfoEvent event) {
        var damager = event.getDamager();

        if (damager instanceof LivingEntity && event.getEntity() instanceof LivingEntity) {
            damageMap.put(damager.getUniqueId(), (LivingEntity) event.getEntity());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onEntityDie(EntityDeathEvent event) {
        damageMap.remove(event.getEntity().getUniqueId());
    }

    public static void clearAggro(LivingEntity entity) {
        var uuid = entity.getUniqueId();

        for (var targetInfo : targetMap.entrySet()) {
            if (!targetInfo.getValue().getUniqueId().equals(uuid)) continue;

            targetInfo.getKey().setTarget(null);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onEntityTarget(EntityTargetLivingEntityEvent event) {
        if (!(event.getEntity() instanceof Mob entity)) return;

        var target = event.getTarget();

        if (target == null) {
            targetMap.remove(entity);
            return;
        }

        targetMap.put(entity, target);
    }

    @EventHandler
    private void onCleanupEntity(EntityCleanupEvent event) {
        if (event.isForced()) {
            if (event.getEntity() instanceof Mob) {
                targetMap.remove(event.getEntity());
            }

            damageMap.remove(event.getUniqueId());
        }
    }
}
