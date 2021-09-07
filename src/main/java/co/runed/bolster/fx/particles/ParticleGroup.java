package co.runed.bolster.fx.particles;

import co.runed.bolster.game.Team;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

public class ParticleGroup {
    private Set<ParticleData> particles = new HashSet<>();

    public ParticleGroup() {

    }

    public ParticleGroup(ParticleData... particles) {
        this.particles.addAll(Arrays.asList(particles));
    }

    public ParticleGroup add(ParticleData container) {
        this.particles.add(container);

        return this;
    }

    public void spawnParticle(Team team, Location location, int count, int offsetX, int offsetY, int offsetZ, double extra) {
        for (var particle : particles) {
            particle.spawnParticle(team, location, count, offsetX, offsetY, offsetZ, extra);
        }
    }

    public void spawnParticle(Player player, Location location, int count, int offsetX, int offsetY, int offsetZ, double extra) {
        this.spawnParticle(Collections.singletonList(player), location, count, offsetX, offsetY, offsetZ, extra);
    }

    public void spawnParticle(World world, Location location, int count, int offsetX, int offsetY, int offsetZ, double extra) {
        for (var particle : particles) {
            particle.spawnParticle(world, location, count, offsetX, offsetY, offsetZ, extra);
        }
    }

    public void spawnParticle(List<Player> viewers, Location location, int count, int offsetX, int offsetY, int offsetZ, double extra) {
        for (var particle : particles) {
            particle.spawnParticle(viewers, location, count, offsetX, offsetY, offsetZ, extra);
        }
    }

//    public void playEffect(Player player, Location location, Effect effect) {
//        effect.setEntity(player);
//        effect.setLocation(location);
//
//        if (data instanceof Particle.DustOptions dustOptions) {
//            effect.particleSize = dustOptions.getSize();
//            effect.color = dustOptions.getColor();
//        }
//
//        Bolster.getEffectManager().start(effect);
//    }
}
