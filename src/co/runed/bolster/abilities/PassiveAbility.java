package co.runed.bolster.abilities;

import co.runed.bolster.Bolster;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public abstract class PassiveAbility extends Ability {
    protected BukkitTask scheduledTask;

    public PassiveAbility(long interval) {
        this.setCooldown(interval);

        Plugin plugin = Bolster.getInstance();

        if(this.getCooldown() > -1) {
            this.scheduledTask = plugin.getServer().getScheduler().runTaskTimer(plugin, this::activate, 0L, (this.getCooldown() * 20));
        }
    }

    @Override
    public void destroy() {
        super.destroy();

        if (this.scheduledTask != null) {
            this.scheduledTask.cancel();
        }
    }
}
