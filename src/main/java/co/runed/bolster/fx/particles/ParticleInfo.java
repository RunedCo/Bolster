package co.runed.bolster.fx.particles;

import co.runed.bolster.Bolster;
import co.runed.bolster.game.Team;
import de.slikey.effectlib.Effect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.EnumSet;
import java.util.Set;

public class ParticleInfo {
    Set<Particle> particles;
    Object data;

    public ParticleInfo(Particle particle) {
        this(particle, null);
    }

    public ParticleInfo(Particle particle, Object data) {
        this(EnumSet.of(particle), data);
    }

    public ParticleInfo(Set<Particle> particles, Object data) {
        this.particles = particles;
        this.data = data;
    }

    public void spawnParticle(Team team, Location location, int count, int offsetX, int offsetY, int offsetZ) {
        for (var entity : team.getMembers()) {
            if (entity instanceof Player)
                this.spawnParticle((Player) entity, location, count, offsetX, offsetY, offsetZ);
        }
    }

    public void spawnParticle(Player player, Location location, int count, int offsetX, int offsetY, int offsetZ) {
        var countPer = count / this.particles.size();

        for (var particle : this.particles) {
            player.spawnParticle(particle, location, countPer, offsetX, offsetY, offsetZ, this.data);
        }
    }

    public void spawnParticle(World world, Location location, int count, int offsetX, int offsetY, int offsetZ) {
        var countPer = count / this.particles.size();

        for (var particle : this.particles) {
            world.spawnParticle(particle, location, countPer, offsetX, offsetY, offsetZ, this.data);
        }
    }

    public void playEffect(Player player, Location location, Effect effect) {
        effect.setEntity(player);
        effect.setLocation(location);

        if (data instanceof Particle.DustOptions) {
            var dustOptions = (Particle.DustOptions) data;
            effect.particleSize = dustOptions.getSize();
            effect.color = dustOptions.getColor();
        }

        Bolster.getEffectManager().start(effect);
    }
}
