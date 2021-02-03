package co.runed.bolster.wip.particles;

import co.runed.bolster.util.IConfigurable;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.registries.IRegisterable;
import co.runed.bolster.util.registries.Registries;
import org.bukkit.configuration.ConfigurationSection;

public class ParticleSet extends Properties implements IRegisterable, IConfigurable
{
    String name;

    @Override
    public String getId()
    {
        return Registries.PARTICLE_SETS.getId(this);
    }

    @Override
    public String getDescription()
    {
        return null;
    }

    @Override
    public void create(ConfigurationSection config)
    {
        this.name = config.getString("name", "");
    }
}
