package co.runed.bolster.fx.particles;

import co.runed.bolster.game.Team;
import com.destroystokyo.paper.ParticleBuilder;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ParticleData {
    private final Particle particle;
    private Object data = null;
    private Color color = null;
    private Color endColor = null;
    private float size = 1f;
    private double extra = 1;
    private int count = -1;
    private int note = 0;

    public ParticleData(Particle particle) {
        this.particle = particle;
    }

    public ParticleData setColor(Color color) {
        this.color = color;

        return this;
    }

    public ParticleData setEndColor(Color color) {
        this.endColor = color;

        return this;
    }

    public ParticleData setData(Object data) {
        this.data = data;

        return this;
    }

    public ParticleData setSize(float size) {
        this.size = size;

        return this;
    }

    public ParticleData setCount(int count) {
        this.count = count;

        return this;
    }

    public ParticleData setSpeed(double speed) {
        this.extra = speed;

        return this;
    }

    public ParticleData setNote(int note) {
        this.note = note;

        return this;
    }

    public void spawnParticle(Team team, Location location, int count, int offsetX, int offsetY, int offsetZ, double extra) {
        var players = team.getMembers().stream().filter(e -> e instanceof Player).map(e -> (Player) e).collect(Collectors.toList());

        this.spawnParticle(players, location, count, offsetX, offsetY, offsetZ, extra);
    }

    public void spawnParticle(Player player, Location location, int count, int offsetX, int offsetY, int offsetZ, double extra) {
        this.spawnParticle(Collections.singletonList(player), location, count, offsetX, offsetY, offsetZ, extra);
    }

    public void spawnParticle(World world, Location location, int count, int offsetX, int offsetY, int offsetZ, double extra) {
        this.spawnParticle((List<Player>) null, location, count, offsetX, offsetY, offsetZ, extra);
    }

    public void spawnParticle(List<Player> recievers, Location location, int count, int offsetX, int offsetY, int offsetZ, double extra) {
        var builder = new ParticleBuilder(particle)
                .receivers(recievers)
                .location(location)
                .offset(offsetX, offsetY, offsetZ)
                .count(this.count != -1 ? this.count : count)
                .extra(extra);

        switch (particle) {
            case SPELL_MOB_AMBIENT, SPELL_MOB -> {
                var red = color.getRed() / 255D;
                var green = color.getGreen() / 255D;
                var blue = color.getBlue() / 255D;

                builder = builder.offset(red, green, blue)
                        .extra(1);
            }
            case REDSTONE -> builder = builder.color(color, size);
            case NOTE -> builder = builder.offset(0, note / 24D, 0)
                    .extra(1);
        }

        builder.spawn();
    }
}
