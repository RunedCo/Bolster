package co.runed.bolster.particles;

import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.registries.IRegisterable;

public class ParticleSet extends Properties implements IRegisterable
{
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
}
