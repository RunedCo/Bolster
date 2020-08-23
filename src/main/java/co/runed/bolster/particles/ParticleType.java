package co.runed.bolster.particles;

import co.runed.bolster.util.properties.Property;

public class ParticleType extends Property<ParticleInfo>
{
    public ParticleType(String id, ParticleInfo particle)
    {
        this(id);

        this.setDefault(particle);
    }

    public ParticleType(String id)
    {
        super(id);
    }
}
