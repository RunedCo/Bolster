package co.runed.bolster.abilities.targeted;

import co.runed.bolster.BolsterEntity;
import co.runed.bolster.abilities.TargetedAbility;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import org.bukkit.util.Vector;

public class SetVelocityAbility extends TargetedAbility<BolsterEntity>
{
    Vector velocity;

    public SetVelocityAbility(Target<BolsterEntity> target, Vector velocity)
    {
        super(target);

        this.velocity = velocity;
    }

    @Override
    public void onActivate(Properties properties)
    {
        this.getTarget().get(properties).getBukkit().setVelocity(velocity);
    }
}
