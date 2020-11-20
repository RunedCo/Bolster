package co.runed.bolster.particles;

import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.registries.IRegisterable;
import org.bukkit.configuration.ConfigurationSection;

public class ParticleSet extends Properties implements IRegisterable
{
    String name;
    String id;

    @Override
    public void setId(String id)
    {
        this.id = id;
    }

    @Override
    public String getId()
    {
        return this.id;
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
