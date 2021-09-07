package co.runed.bolster.fx.particles;

import co.runed.dayroom.properties.Property;

public class ParticleType extends Property<ParticleGroup> {
    public ParticleType(String id, ParticleGroup defaultParticle) {
        this(id);

        this.setDefault(defaultParticle);
    }

    public ParticleType(String id) {
        super(id);
    }
}
