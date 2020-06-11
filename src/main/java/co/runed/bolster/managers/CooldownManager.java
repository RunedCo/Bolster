package co.runed.bolster.managers;

import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class CooldownManager {
    private final Plugin plugin;
    private final List<CooldownData> cooldowns = new ArrayList<>();

    public CooldownManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void setCooldown(LivingEntity entity, String source, long cooldown) {
        if(cooldown <= 0) return;

        if(this.getRemainingTime(entity, source) <= 0) {
            this.clearCooldown(entity, source);
            this.cooldowns.add(new CooldownData(entity, source, Instant.now(), cooldown));
        }
    }

    public void clearAllFrom(LivingEntity entity) {
        this.cooldowns.removeIf(cd -> cd.caster.equals(entity));
    }

    public void clearCooldown(LivingEntity entity, String source) {
        this.cooldowns.removeIf(cd -> cd.source.equals(source) && cd.caster.equals(entity));
    }

    public long getRemainingTime(LivingEntity entity, String source) {
        for (CooldownData cd : this.cooldowns) {
            if (cd.caster.equals(entity) && cd.source.equals(source)) {
                return cd.getRemainingTime();
            }
        }

        return -1;
    }

    private static class CooldownData {
        private final LivingEntity caster;
        private final String source;
        private final Instant castTime;
        private final long cooldown;

        private CooldownData(LivingEntity entity, String source, Instant castTime, long cooldown) {
            this.caster = entity;
            this.source = source;
            this.castTime = castTime;
            this.cooldown = cooldown;
        }

        public long getRemainingTime() {
            Duration sinceStart = Duration.between(this.castTime, Instant.now());
            Duration remaining = Duration.ofSeconds(this.cooldown).minus(sinceStart);

            return remaining.getSeconds();
        }
    }
}
