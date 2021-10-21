package co.runed.bolster.managers;

import co.runed.bolster.entity.BolsterEntity;
import co.runed.bolster.events.entity.EntityCleanupEvent;
import co.runed.bolster.events.entity.EntitySetCooldownEvent;
import co.runed.bolster.events.player.LoadPlayerDataEvent;
import co.runed.bolster.game.traits.Traits;
import co.runed.bolster.util.BukkitUtil;
import co.runed.bolster.util.TimeUtil;
import co.runed.bolster.util.cooldown.CooldownSource;
import co.runed.bolster.wip.Cooldown;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;

import java.text.DecimalFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CooldownManager extends Manager {
    private final List<Cooldown> cooldowns = new ArrayList<>();

    private static final DecimalFormat decimalFormatter = new DecimalFormat("#.#");
    private static CooldownManager _instance;

    public CooldownManager(Plugin plugin) {
        super(plugin);

        _instance = this;
    }

    /**
     * Sets the cooldown for a specific cooldown source for an entity
     *
     * @param entity   the entity
     * @param source   the source
     * @param cooldown the cooldown in seconds
     */
    public void setCooldown(LivingEntity entity, CooldownSource source, int slot, double cooldown, boolean trigger, boolean isGlobal) {
        this.setCooldown(entity, source.getCooldownId(), slot, cooldown, trigger, isGlobal);
    }

    public void setCooldown(LivingEntity entity, String cooldownId, int slot, double cooldown, boolean trigger, boolean isGlobal) {
        cooldown = Math.max(cooldown, 0);

        cooldown = cooldown * Math.max(0, 1 - BolsterEntity.from(entity).getTrait(Traits.COOLDOWN_REDUCTION_PERCENT));

        this.cooldowns.removeIf(cd -> cd.getId().equals(cooldownId) && cd.getOwner().equals(entity.getUniqueId()) && cd.getSlot() == slot);

        var castTime = Instant.now();

        if (this.getRemainingTime(entity, cooldownId, slot) <= 0) {
            var cooldownObj = new Cooldown()
                    .setGlobal(isGlobal)
                    .withSlot(slot)
                    .withOwner(entity)
                    .withId(cooldownId)
                    .ofDuration(TimeUtil.fromSeconds(cooldown));

            cooldowns.add(cooldownObj);

//            this.cooldowns.add(new CooldownData(entity, cooldownId, slot, castTime, (long) (cooldown * 1000), isGlobal));
        }

        if (trigger) {
            BukkitUtil.triggerEvent(new EntitySetCooldownEvent(entity, castTime, cooldownId, slot, cooldown, isGlobal));
        }
    }

    /**
     * Clear all cooldowns from an entity
     *
     * @param entity the entity
     */
    public void clearAllFrom(LivingEntity entity) {
        var cds = this.cooldowns.stream().filter(cd -> cd.getOwner().equals(entity.getUniqueId())).collect(Collectors.toList());

        for (var data : cds) {
            this.clearCooldown(entity, data.getId(), data.getSlot());
        }
    }

    /**
     * Clear one cooldown from every source from an entity
     *
     * @param entity the entity
     */
    public void clearOneChargeFromAll(LivingEntity entity) {
        List<String> cleared = new ArrayList<>();
        // todo sort by lowest
        var cds = this.cooldowns.stream().filter(cd -> cd.getOwner().equals(entity.getUniqueId())).collect(Collectors.toList());

        for (var data : cds) {
            if (cleared.contains(data.getId())) continue;

            this.clearCooldown(entity, data.getId(), data.getSlot());

            cleared.add(data.getId());
        }
    }

    /**
     * Clear a specific cooldown from an entity in a specific slot
     *
     * @param entity the entity
     * @param source the source
     */
    public void clearCooldown(LivingEntity entity, CooldownSource source, int slot) {
        clearCooldown(entity, source.getCooldownId(), slot);
    }

    public void clearCooldown(LivingEntity entity, String cooldownId, int slot) {
        this.setCooldown(entity, cooldownId, slot, 0, true, false);
    }

    /**
     * Clear a specific cooldown from an entity in all slots
     *
     * @param entity the entity
     * @param source the source
     */
    public void clearCooldown(LivingEntity entity, CooldownSource source) {
        this.clearCooldown(entity, source.getCooldownId());
    }

    public void clearCooldown(LivingEntity entity, String cooldownId) {
        var cds = this.cooldowns.stream().filter(cd -> cd.getOwner().equals(entity.getUniqueId()) && cd.getId() == cooldownId).collect(Collectors.toList());

        for (var data : cds) {
            this.clearCooldown(entity, cooldownId, data.getSlot());
        }
    }

    /**
     * Get the amount of time remaining an entity's specific cooldown source
     *
     * @param entity the entity
     * @param source the source
     * @return the time remaining in seconds
     */
    public double getRemainingTime(LivingEntity entity, CooldownSource source, int slot) {
        return getRemainingTime(entity, source.getCooldownId(), slot);
    }

    public double getRemainingTime(LivingEntity entity, String cooldownId, int slot) {
        for (var cd : this.cooldowns) {
            if (cd.getOwner().equals(entity.getUniqueId()) && cd.getId().equals(cooldownId) && cd.getSlot() == slot) {
                return cd.getRemainingTime();
            }
        }

        return -1;
    }

    public List<Cooldown> getCooldownData(LivingEntity entity) {
        return this.cooldowns.stream().filter(cd -> cd.getOwner().equals(entity.getUniqueId())).collect(Collectors.toList());
    }

    public void cleanupAll(boolean force) {
        this.cooldowns.removeIf(cd -> cd.isGlobal() || (cd.isDone() || force));
    }

    public void cleanup(LivingEntity entity, boolean force) {
        this.cleanup(entity.getUniqueId(), force);
    }

    public void cleanup(UUID uuid, boolean force) {
        var isPlayerOnline = true;

        this.cooldowns.removeIf(cd -> cd.getOwner().equals(uuid) && ((cd.isGlobal() && isPlayerOnline) || (cd.isDone() || force)));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onLoadPlayer(LoadPlayerDataEvent event) {
        var playerData = event.getPlayerData();

        for (var data : playerData.getGlobalCooldowns()) {
            data.withOwner(playerData.getUuid());

            this.cooldowns.add(data);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onCleanupEntity(EntityCleanupEvent event) {
        this.cleanup(event.getUniqueId(), event.isForced());
    }

    public static String formatCooldown(double cooldown) {
        var formattedCooldown = "" + (int) cooldown;
        if (cooldown < 1) {
            formattedCooldown = decimalFormatter.format(cooldown);
        }

        return formattedCooldown;
    }

//    public static class CooldownData {
//        @JsonExclude
//        private UUID casterUUID;
//
//        final String cooldownId;
//        final Instant castTime;
//        final long cooldown;
//        final boolean isGlobal;
//        int slot;
//
//        public CooldownData(LivingEntity entity, String cooldownId, int slot, Instant castTime, long cooldownMs, boolean isGlobal) {
//            this.casterUUID = entity.getUniqueId();
//            this.cooldownId = cooldownId;
//            this.castTime = castTime;
//            this.cooldown = cooldownMs;
//            this.slot = slot;
//            this.isGlobal = isGlobal;
//        }
//
//        /**
//         * Get remaining time for a cooldown in ms
//         *
//         * @return the remaining time in milliseconds
//         */
//        public long getRemainingTime() {
//            var sinceStart = Duration.between(this.castTime, Instant.now());
//            var remaining = Duration.ofMillis(this.cooldown).minus(sinceStart);
//
//            return remaining.toMillis();
//        }
//
//        public boolean isDone() {
//            return this.getRemainingTime() <= 0;
//        }
//    }

    public static CooldownManager getInstance() {
        return _instance;
    }
}
