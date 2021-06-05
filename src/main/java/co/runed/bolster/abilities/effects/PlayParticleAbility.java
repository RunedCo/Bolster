package co.runed.bolster.abilities.effects;

import co.runed.bolster.BolsterEntity;
import co.runed.bolster.abilities.TargetedAbility;
import co.runed.bolster.conditions.IsEntityTypeCondition;
import co.runed.bolster.fx.particles.ParticleInfo;
import co.runed.bolster.fx.particles.ParticleType;
import co.runed.bolster.game.PlayerData;
import co.runed.bolster.managers.PlayerManager;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import de.slikey.effectlib.Effect;
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
    Effect effect = null;

    public PlayParticleAbility(Target<BolsterEntity> target, ParticleType type, Target<Location> location, Effect effect)
    {
        super(target);

        this.type = type;
        this.location = location;

        this.addCondition(new IsEntityTypeCondition(Target.CASTER, EntityType.PLAYER));
        this.addCondition(new IsEntityTypeCondition(target, EntityType.PLAYER));
    }

    public PlayParticleAbility(Target<BolsterEntity> target, ParticleType type, Target<Location> location, int count, int offsetX, int offsetY, int offsetZ)
    {
        this(target, type, location);

        this.count = count;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
    }

    private PlayParticleAbility(Target<BolsterEntity> target, ParticleType type, Target<Location> location)
    {
        super(target);

        this.type = type;
        this.location = location;

        this.addCondition(new IsEntityTypeCondition(Target.CASTER, EntityType.PLAYER));
        this.addCondition(new IsEntityTypeCondition(target, EntityType.PLAYER));
    }

    @Override
    public void onActivate(Properties properties)
    {
        Player caster = (Player) this.getCaster();
        PlayerData data = PlayerManager.getInstance().getPlayerData(caster);

        Player target = (Player) this.getTarget().get(properties).getBukkit();

        ParticleInfo info = data.getActiveParticleSet().get(this.type);

        if (effect != null)
        {
            info.playEffect(target, location.get(properties), effect);
            return;
        }

        info.spawnParticle(target, location.get(properties), count, offsetX, offsetY, offsetZ);
    }
}
