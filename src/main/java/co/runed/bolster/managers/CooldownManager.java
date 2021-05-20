package co.runed.bolster.managers;

import co.runed.bolster.BolsterEntity;
import co.runed.bolster.game.Traits;
import co.runed.bolster.util.cooldown.ICooldownSource;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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

    public void setCooldown(LivingEntity entity, ICooldownSource source, int slot, double cooldown)
    {
        this.setCooldown(entity, source, slot, cooldown, true);
    }

    /**
     * Sets the cooldown for a specific cooldown source for an entity
     *
     * @param entity   the entity
     * @param source   the source
     * @param cooldown the cooldown in seconds
     */
    public void setCooldown(LivingEntity entity, ICooldownSource source, int slot, double cooldown, boolean trigger)
    {
        cooldown = Math.max(cooldown, 0);

        cooldown = cooldown * Math.max(0, 1 - BolsterEntity.from(entity).getTrait(Traits.COOLDOWN_REDUCTION_PERCENT));

        this.cooldowns.removeIf(cd -> cd.source.getCooldownId().equals(source.getCooldownId()) && cd.caster.equals(entity) && cd.slot == slot);

        if (this.getRemainingTime(entity, source, slot) <= 0)
        {
            this.cooldowns.add(new CooldownData(entity, source, slot, Instant.now(), (long) (cooldown * 1000)));
        }

        if (trigger) source.onToggleCooldown();
    }

    /**
     * Clear all cooldowns from an entity
     *
     * @param entity the entity
     */
    public void clearAllFrom(LivingEntity entity)
    {
        List<CooldownData> cds = this.cooldowns.stream().filter(cd -> cd.caster.equals(entity)).collect(Collectors.toList());

        for (CooldownData data : cds)
        {
            this.clearCooldown(entity, data.source, data.slot);
        }
    }

    /**
     * Clear one cooldown from every source from an entity
     *
     * @param entity the entity
     */
    public void clearOneChargeFrom(LivingEntity entity)
    {
        List<String> cleared = new ArrayList<>();
        // todo sort by lowest
        List<CooldownData> cds = this.cooldowns.stream().filter(cd -> cd.caster.equals(entity)).collect(Collectors.toList());

        for (CooldownData data : cds)
        {
            if (cleared.contains(data.source.getCooldownId())) continue;

            this.clearCooldown(entity, data.source, data.slot);

            cleared.add(data.source.getCooldownId());
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
        this.setCooldown(entity, source, slot, 0);
    }

    /**
     * Clear a specific cooldown from an entity in all slots
     *
     * @param entity the entity
     * @param source the source
     */
    public void clearCooldown(LivingEntity entity, ICooldownSource source)
    {
        List<CooldownData> cds = this.cooldowns.stream().filter(cd -> cd.caster.equals(entity) && cd.source == source).collect(Collectors.toList());

        for (CooldownData data : cds)
        {
            this.clearCooldown(entity, data.source, data.slot);
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
        for (CooldownData cd : this.cooldowns)
        {
            if (cd.caster.equals(entity) && cd.source.getCooldownId().equals(source.getCooldownId()) && cd.slot == slot)
            {
                return cd.getRemainingTime() / 1000d;
            }
        }

        return -1;
    }

    private static class CooldownData
    {
        private final LivingEntity caster;
        private final ICooldownSource source;
        private final Instant castTime;
        private final long cooldown;
        private int slot = 0;

        private CooldownData(LivingEntity entity, ICooldownSource source, int slot, Instant castTime, long cooldownMs)
        {
            this.caster = entity;
            this.source = source;
            this.castTime = castTime;
            this.cooldown = cooldownMs;
            this.slot = slot;
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
    }

    public static CooldownManager getInstance()
    {
        return _instance;
    }
}
