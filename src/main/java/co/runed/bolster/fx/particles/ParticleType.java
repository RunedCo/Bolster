package co.runed.bolster.fx.particles;

import co.runed.dayroom.properties.Property;

public class ParticleType extends Property<ParticleInfo> {
    private ParticleInfo info;

    public ParticleType(String id, ParticleInfo particle) {
        this(id);

        this.setDefault(particle);
    }

    public ParticleType(String id) {
        super(id);
    }
}
