package co.runed.bolster.particles;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

import java.util.EnumSet;
import java.util.Set;

public class ParticleInfo
{
    Set<Particle> particles;
    Object data;

    public ParticleInfo(Particle particle)
    {
        this(particle, null);
    }

    public ParticleInfo(Particle particle, Object data)
    {
        this(EnumSet.of(particle), data);
    }

    public ParticleInfo(Set<Particle> particles, Object data)
    {
        this.particles = particles;
        this.data = data;
    }

    public void spawnParticle(World world, Location location, int count, int offsetX, int offsetY, int offsetZ)
    {
        int countPer = count / this.particles.size();

        for (Particle particle : this.particles)
        {
            world.spawnParticle(particle, location, countPer, offsetX, offsetY, offsetZ, this.data);
        }
    }
}
