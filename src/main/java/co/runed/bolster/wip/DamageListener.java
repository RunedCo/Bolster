package co.runed.bolster.wip;

import co.runed.bolster.Bolster;
import co.runed.bolster.damage.DamageInfo;
import co.runed.bolster.damage.DamageSource;
import co.runed.bolster.entity.BolsterEntity;
import co.runed.bolster.events.entity.EntityDamageInfoEvent;
import co.runed.bolster.managers.Manager;
import co.runed.bolster.match.PlayerDamageMatchHistoryEvent;
import co.runed.bolster.match.PlayerKillMatchHistoryEvent;
import co.runed.bolster.util.BukkitUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class DamageListener extends Manager {
    private static DamageListener _instance;

    public Map<UUID, Set<DamageInfo>> damageInfoMap = new HashMap<>();
    public Map<UUID, DamageInfo> lastDamageInfo = new HashMap<>();

    public DamageListener(Plugin plugin) {
        super(plugin);

        _instance = this;
    }

    @Nullable
    public DamageInfo getLastDamageInfo(Entity entity) {
        return lastDamageInfo.getOrDefault(entity.getUniqueId(), null);
    }

    public void queueDamageInfo(Entity entity, DamageInfo info) {
        damageInfoMap.putIfAbsent(entity.getUniqueId(), new HashSet<>());
        var infoSet = damageInfoMap.get(entity.getUniqueId());
        infoSet.add(info);
    }

    private DamageInfo getDamageInfo(Entity entity, double damage, LivingEntity attacker) {
        var infoSet = damageInfoMap.getOrDefault(entity.getUniqueId(), new HashSet<>());

        for (var info : new HashSet<>(infoSet)) {
            var hasAttacker = info.getAttacker() != null;
            var isAttacker = hasAttacker && attacker != null && info.getAttacker().getUniqueId().equals(attacker.getUniqueId());

            if (info.getDamage() == damage && (!hasAttacker || isAttacker)) {
                return info;
            }
        }

        return null;
    }

    private void removeDamageInfo(Entity entity, DamageInfo info) {
        var infoSet = damageInfoMap.getOrDefault(entity.getUniqueId(), new HashSet<>());
        infoSet.remove(info);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onDamage(EntityDamageEvent event) {
        var originalDamage = event.getOriginalDamage(EntityDamageEvent.DamageModifier.BASE);
        var target = event.getEntity();
        LivingEntity attacker = null;

        if (event instanceof EntityDamageByEntityEvent byE && byE.getDamager() instanceof LivingEntity le) {
            attacker = le;
        }

        var damageInfo = getDamageInfo(target, originalDamage, attacker);

        if (damageInfo == null) damageInfo = DamageInfo.fromEvent(event);

        var infoEvent = BukkitUtil.triggerEvent(new EntityDamageInfoEvent(target, damageInfo, event));
        if (infoEvent.isCancelled()) event.setCancelled(true);

        if (!event.isCancelled()) {
            lastDamageInfo.put(target.getUniqueId(), damageInfo);
        }

        removeDamageInfo(target, damageInfo);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onDeath(PlayerDeathEvent event) {
        var damageInfo = getLastDamageInfo(event.getEntity());

        if (damageInfo == null) return;

        if (damageInfo.getAttacker() != null && damageInfo.getAttacker() instanceof Player player) {
            Bolster.getActiveGameMode().getMatchHistory().addEvent(new PlayerKillMatchHistoryEvent(player, event.getEntity(), damageInfo));
        }

        Component message = null;
        DamageSource prevNext = null;
        var next = damageInfo.getDamageSource();
        while (message == null && next != null && next != prevNext) {
            message = next.getDeathMessage(damageInfo.getAttacker(), event.getEntity(), damageInfo);

            prevNext = next;
            next = next.next();
        }

        if (message != null) event.deathMessage(message);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onDamage(EntityDamageInfoEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;

        var info = event.getDamageInfo();

        Bolster.getActiveGameMode().getMatchHistory().addEvent(new PlayerDamageMatchHistoryEvent(player, event.getEntity(), info));
        BolsterEntity.from(player).sendDebugMessage("You dealt " + event.getFinalDamage() + " damage of type " + info.getDamageType() + " from source " + info.getDamageSource().getDamageSourceName() + ChatColor.RESET + " to target " + event.getEntity().getName());
    }

    public static DamageListener getInstance() {
        return _instance;
    }
}
