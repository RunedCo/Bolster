package co.runed.bolster.fx.particles.effects;

import co.runed.bolster.fx.particles.ParticleGroup;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public abstract class ParticleEffect {
    public void run(World world, ParticleGroup particle) {
        run((List<Player>) null, particle);
    }

    public void run(Player player, ParticleGroup particle) {
        run(Collections.singletonList(player), particle);
    }

    /***
     * Run the effect
     * @param receivers list of players to send the effect to. set to null for whole world
     * @param particle particle group
     */
    public abstract void run(@Nullable List<Player> receivers, ParticleGroup particle);
}
