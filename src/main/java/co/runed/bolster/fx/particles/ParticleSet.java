package co.runed.bolster.fx.particles;

import co.runed.bolster.util.INameable;
import co.runed.bolster.util.config.IConfigurable;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.registries.IRegisterable;
import co.runed.bolster.util.registries.Registries;
import org.bukkit.configuration.ConfigurationSection;

public class ParticleSet extends Properties implements IRegisterable, IConfigurable, INameable
{
    String name;

    @Override
    public String getId()
    {
        return Registries.PARTICLE_SETS.getId(this);
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void loadConfig(ConfigurationSection config)
    {
        this.name = config.getString("name", "");
    }

    @Override
    public void create()
    {

    }
}
