package co.runed.bolster.managers;

import co.runed.bolster.BolsterEntity;
import co.runed.bolster.game.Traits;
import co.runed.bolster.util.ICooldownSource;
import co.runed.bolster.util.Manager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
    public void setCooldown(LivingEntity entity, ICooldownSource source, double cooldown)
    {
        if (cooldown <= 0) return;

        cooldown = cooldown * Math.max(0, 1 - BolsterEntity.from(entity).getTrait(Traits.COOLDOWN_REDUCTION_PERCENT));

        if (this.getRemainingTime(entity, source) <= 0)
        {
            this.clearCooldown(entity, source);
            this.cooldowns.add(new CooldownData(entity, source, Instant.now(), (long) (cooldown * 1000)));
        }
    }

    /**
     * Clear all cooldowns from an entity
     *
     * @param entity the entity
     */
    public void clearAllFrom(LivingEntity entity)
    {
        this.cooldowns.removeIf(cd -> cd.caster.equals(entity));
    }

    /**
     * Clear a specific cooldown from an entity
     *
     * @param entity the entity
     * @param source the source
     */
    public void clearCooldown(LivingEntity entity, ICooldownSource source)
    {
        this.cooldowns.removeIf(cd -> cd.source.getCooldownId().equals(source.getCooldownId()) && cd.caster.equals(entity));
    }

    /**
     * Get the amount of time remaining an entity's specific cooldown source
     *
     * @param entity the entity
     * @param source the source
     * @return the time remaining in seconds
     */
    public double getRemainingTime(LivingEntity entity, ICooldownSource source)
    {
        for (CooldownData cd : this.cooldowns)
        {
            if (cd.caster.equals(entity) && cd.source.getCooldownId().equals(source.getCooldownId()))
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

        private CooldownData(LivingEntity entity, ICooldownSource source, Instant castTime, long cooldownMs)
        {
            this.caster = entity;
            this.source = source;
            this.castTime = castTime;
            this.cooldown = cooldownMs;
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
