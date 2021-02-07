package co.runed.bolster.abilities.effects;

import co.runed.bolster.BolsterEntity;
import co.runed.bolster.abilities.TargetedAbility;
import co.runed.bolster.conditions.IsEntityTypeCondition;
import co.runed.bolster.managers.PlayerManager;
import co.runed.bolster.game.PlayerData;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import co.runed.bolster.wip.particles.ParticleType;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class PlayParticleAbility extends TargetedAbility<BolsterEntity>
{
    ParticleType type;
    Target<Location> location;
    int count;
    int offsetX;
    int offsetY;
    int offsetZ;

    public PlayParticleAbility(Target<BolsterEntity> target, ParticleType type, Target<Location> location, int count, int offsetX, int offsetY, int offsetZ)
    {
        super(target);

        this.type = type;
        this.location = location;
        this.count = count;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;

        this.addCondition(new IsEntityTypeCondition(Target.CASTER, EntityType.PLAYER));
        this.addCondition(new IsEntityTypeCondition(target, EntityType.PLAYER));
    }

    @Override
    public void onActivate(Properties properties)
    {
        Player caster = (Player) this.getCaster();
        PlayerData data = PlayerManager.getInstance().getPlayerData(caster);

        Player target = (Player) this.getTarget().get(properties).getBukkit();

        data.getActiveParticleSet().get(this.type).spawnParticle(target, location.get(properties), count, offsetX, offsetY, offsetZ);
    }
}
