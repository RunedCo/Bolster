package co.runed.bolster.fx.particles;

import co.runed.bolster.common.properties.Properties;
import co.runed.bolster.common.util.Identifiable;
import co.runed.bolster.common.util.Nameable;
import co.runed.bolster.util.config.Configurable;
import co.runed.bolster.util.registries.Registries;
import org.bukkit.configuration.ConfigurationSection;

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

}
