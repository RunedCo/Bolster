package co.runed.bolster.fx.particles.effects;

import co.runed.bolster.fx.particles.ParticleGroup;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LineParticleEffect extends ParticleEffect {
    private final Location from;
    private final Location to;
    private final int steps;

    public LineParticleEffect(Location from, double length, int steps) {
        this(from, from.clone().add(from.getDirection().normalize().multiply(length)), steps);
    }

    public LineParticleEffect(Location from, Location to, int steps) {
        this.from = from;
        this.to = to;
        this.steps = steps;
    }

    @Override
    public void run(@Nullable List<Player> receivers, ParticleGroup particle) {
        var link = to.toVector().subtract(from.toVector());
        var length = (float) link.length();
        link.normalize();
        var ratio = length / (float) this.steps;
        var v = link.multiply(ratio);
        var loc = from.clone().subtract(v);

        for (var i = 0; i < steps; ++i) {
            loc.add(v);
            particle.spawnParticle(receivers, loc, 0, 0, 0, 0, 1);
        }
    }
}
