package co.runed.bolster.abilities.effects;

import co.runed.bolster.abilities.TargetedAbility;
import co.runed.bolster.managers.PlayerManager;
import co.runed.bolster.game.PlayerData;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import co.runed.bolster.wip.particles.ParticleType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayGlobalParticleAbility extends TargetedAbility<Location>
{
    ParticleType type;
    int count;
    int offsetX;
    int offsetY;
    int offsetZ;

    public PlayGlobalParticleAbility(ParticleType type, Target<Location> target, int count, int offsetX, int offsetY, int offsetZ)
    {
        super(target);

        this.type = type;
        this.count = count;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
    }

    @Override
    public void onActivate(Properties properties)
    {
        Player caster = (Player) this.getCaster();
        PlayerData data = PlayerManager.getInstance().getPlayerData(caster);
        Location location = this.getTarget().get(properties);

        data.getActiveParticleSet().get(this.type).spawnParticle(location.getWorld(), location, count, offsetX, offsetY, offsetZ);
    }
}
