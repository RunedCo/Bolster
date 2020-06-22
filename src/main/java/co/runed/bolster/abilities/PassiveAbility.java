package co.runed.bolster.abilities;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.properties.AbilityProperties;
import co.runed.bolster.properties.Properties;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public abstract class PassiveAbility extends Ability {
    protected BukkitTask scheduledTask;

    public PassiveAbility(long tickInterval) {
        this.setCooldownTicks(tickInterval);

        Plugin plugin = Bolster.getInstance();

        if(this.getTotalCooldown() > -1) {
            this.scheduledTask = plugin.getServer().getScheduler().runTaskTimer(plugin, this::run, 0L, tickInterval);
        }
    }

    private void run() {
        Properties properties = new Properties();
        properties.set(AbilityProperties.CASTER, this.getCaster());
        properties.set(AbilityProperties.WORLD, this.getCaster().getWorld());

        this.activate(properties);
    }

    @Override
    public void destroy() {
        super.destroy();

        if (this.scheduledTask != null) {
            this.scheduledTask.cancel();
        }
    }
}
