package co.runed.bolster;

import org.bukkit.entity.Player;
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

    public void setCooldown(Player player, String source, long cooldown) {
        if(cooldown <= 0) return;

        if(this.getRemainingTime(player, source) <= 0) {
            this.clearCooldown(player, source);
            this.cooldowns.add(new CooldownData(player, source, Instant.now(), cooldown));
        }
    }

    public void clearAllFrom(Player player) {
        this.cooldowns.removeIf(cd -> cd.caster.equals(player));
    }

    public void clearCooldown(Player player, String source) {
        this.cooldowns.removeIf(cd -> cd.source.equals(source) && cd.caster.equals(player));
    }

    public long getRemainingTime(Player player, String source) {
        for (CooldownData cd : this.cooldowns) {
            if (cd.caster.equals(player) && cd.source.equals(source)) {
                return cd.getRemainingTime();
            }
        }

        return -1;
    }

    private static class CooldownData {
        private final Player caster;
        private final String source;
        private final Instant castTime;
        private final long cooldown;

        private CooldownData(Player player, String source, Instant castTime, long cooldown) {
            this.caster = player;
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
