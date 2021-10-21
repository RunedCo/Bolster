package co.runed.bolster.fx.particles;

import co.runed.bolster.managers.PlayerManager;
import co.runed.bolster.util.config.Configurable;
import co.runed.bolster.util.registries.Registries;
import co.runed.dayroom.properties.Properties;
import co.runed.dayroom.util.Identifiable;
import co.runed.dayroom.util.Nameable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class ParticleSet extends Properties implements Identifiable, Configurable, Nameable {
    String name;

    @Override
    public String getId() {
        return Registries.PARTICLE_SETS.getId(this);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void loadConfig(ConfigurationSection config) {
        this.name = config.getString("name", "");
    }

    public static ParticleSet getActive(LivingEntity entity) {
        if (entity instanceof Player player) {
            return PlayerManager.getInstance().getPlayerData(player).getActiveParticleSet();
        }

        return new ParticleSet();
    }
}
