package co.runed.bolster.managers;

import co.runed.bolster.BolsterEntity;
import co.runed.bolster.events.CleanupEntityDataEvent;
import co.runed.bolster.events.EntitySetCooldownEvent;
import co.runed.bolster.events.LoadPlayerDataEvent;
import co.runed.bolster.game.PlayerData;
import co.runed.bolster.game.Traits;
import co.runed.bolster.util.cooldown.ICooldownSource;
import co.runed.bolster.util.json.JsonExclude;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CooldownManager extends Manager
{
    private final List<CooldownData> cooldowns = new ArrayList<>();

    private static CooldownManager _instance;

    public CooldownManager(Plugin plugin)
    {
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
    public void setCooldown(LivingEntity entity, ICooldownSource source, int slot, double cooldown, boolean trigger, boolean isGlobal)
    {
        this.setCooldown(entity, source.getCooldownId(), slot, cooldown, trigger, isGlobal);
    }

    public void setCooldown(LivingEntity entity, String cooldownId, int slot, double cooldown, boolean trigger, boolean isGlobal)
    {
        cooldown = Math.max(cooldown, 0);

        cooldown = cooldown * Math.max(0, 1 - BolsterEntity.from(entity).getTrait(Traits.COOLDOWN_REDUCTION_PERCENT));

        this.cooldowns.removeIf(cd -> cd.cooldownId.equals(cooldownId) && cd.casterUUID.equals(entity.getUniqueId()) && cd.slot == slot);

        Instant castTime = Instant.now();

        if (this.getRemainingTime(entity, cooldownId, slot) <= 0)
        {
            this.cooldowns.add(new CooldownData(entity, cooldownId, slot, castTime, (long) (cooldown * 1000), isGlobal));
        }

        if (trigger)
        {
            Bukkit.getServer().getPluginManager().callEvent(new EntitySetCooldownEvent(entity, castTime, cooldownId, slot, cooldown, isGlobal));
        }
    }

    /**
     * Clear all cooldowns from an entity
     *
     * @param entity the entity
     */
    public void clearAllFrom(LivingEntity entity)
    {
        List<CooldownData> cds = this.cooldowns.stream().filter(cd -> cd.casterUUID.equals(entity.getUniqueId())).collect(Collectors.toList());

        for (CooldownData data : cds)
        {
            this.clearCooldown(entity, data.cooldownId, data.slot);
        }
    }

    /**
     * Clear one cooldown from every source from an entity
     *
     * @param entity the entity
     */
    public void clearOneChargeFromAll(LivingEntity entity)
    {
        List<String> cleared = new ArrayList<>();
        // todo sort by lowest
        List<CooldownData> cds = this.cooldowns.stream().filter(cd -> cd.casterUUID.equals(entity.getUniqueId())).collect(Collectors.toList());

        for (CooldownData data : cds)
        {
            if (cleared.contains(data.cooldownId)) continue;

            this.clearCooldown(entity, data.cooldownId, data.slot);

            cleared.add(data.cooldownId);
        }
    }

    /**
     * Clear a specific cooldown from an entity in a specific slot
     *
     * @param entity the entity
     * @param source the source
     */
    public void clearCooldown(LivingEntity entity, ICooldownSource source, int slot)
    {
        clearCooldown(entity, source.getCooldownId(), slot);
    }

    public void clearCooldown(LivingEntity entity, String cooldownId, int slot)
    {
        this.setCooldown(entity, cooldownId, slot, 0, true, false);
    }

    /**
     * Clear a specific cooldown from an entity in all slots
     *
     * @param entity the entity
     * @param source the source
     */
    public void clearCooldown(LivingEntity entity, ICooldownSource source)
    {
        this.clearCooldown(entity, source.getCooldownId());
    }

    public void clearCooldown(LivingEntity entity, String cooldownId)
    {
        List<CooldownData> cds = this.cooldowns.stream().filter(cd -> cd.casterUUID.equals(entity.getUniqueId()) && cd.cooldownId == cooldownId).collect(Collectors.toList());

        for (CooldownData data : cds)
        {
            this.clearCooldown(entity, cooldownId, data.slot);
        }
    }

    /**
     * Get the amount of time remaining an entity's specific cooldown source
     *
     * @param entity the entity
     * @param source the source
     * @return the time remaining in seconds
     */
    public double getRemainingTime(LivingEntity entity, ICooldownSource source, int slot)
    {
        return getRemainingTime(entity, source.getCooldownId(), slot);
    }

    public double getRemainingTime(LivingEntity entity, String cooldownId, int slot)
    {
        for (CooldownData cd : this.cooldowns)
        {
            if (cd.casterUUID.equals(entity.getUniqueId()) && cd.cooldownId.equals(cooldownId) && cd.slot == slot)
            {
                return cd.getRemainingTime() / 1000d;
            }
        }

        return -1;
    }

    public List<CooldownData> getCooldownData(LivingEntity entity)
    {
        return this.cooldowns.stream().filter(cd -> cd.casterUUID.equals(entity.getUniqueId())).collect(Collectors.toList());
    }

    public void cleanupAll(boolean force)
    {
        this.cooldowns.removeIf(cd -> cd.isGlobal || (cd.isDone() || force));
    }

    public void cleanup(LivingEntity entity, boolean force)
    {
        this.cooldowns.removeIf(cd -> cd.casterUUID.equals(entity.getUniqueId()) && (cd.isGlobal || (cd.isDone() || force)));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onLoadPlayer(LoadPlayerDataEvent event)
    {
        PlayerData playerData = event.getPlayerData();

        for (CooldownData data : playerData.getGlobalCooldowns())
        {
            data.casterUUID = playerData.getUuid();
            this.cooldowns.add(data);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onCleanupEntity(CleanupEntityDataEvent event)
    {
        this.cleanup(event.getEntity(), event.isForced());
    }

    public static class CooldownData
    {
        @JsonExclude
        private UUID casterUUID;

        final String cooldownId;
        final Instant castTime;
        final long cooldown;
        final boolean isGlobal;
        int slot;

        public CooldownData(LivingEntity entity, String cooldownId, int slot, Instant castTime, long cooldownMs, boolean isGlobal)
        {
            this.casterUUID = entity.getUniqueId();
            this.cooldownId = cooldownId;
            this.castTime = castTime;
            this.cooldown = cooldownMs;
            this.slot = slot;
            this.isGlobal = isGlobal;
        }

        /**
         * Get remaining time for a cooldown in ms
         *
         * @return the remaining time in milliseconds
         */
        public long getRemainingTime()
        {
            Duration sinceStart = Duration.between(this.castTime, Instant.now());
            Duration remaining = Duration.ofMillis(this.cooldown).minus(sinceStart);

            return remaining.toMillis();
        }

        public boolean isDone()
        {
            return this.getRemainingTime() <= 0;
        }
    }

    public static CooldownManager getInstance()
    {
        return _instance;
    }
}
