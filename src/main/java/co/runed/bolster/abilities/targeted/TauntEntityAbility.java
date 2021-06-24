package co.runed.bolster.abilities.targeted;

import co.runed.bolster.abilities.TargetedAbility;
import co.runed.bolster.entity.BolsterEntity;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;

public class TauntEntityAbility extends TargetedAbility<BolsterEntity>
{
    Target<BolsterEntity> taunted;

    public TauntEntityAbility(Target<BolsterEntity> taunted)
    {
        this(Target.CASTER, taunted);
    }

    public TauntEntityAbility(Target<BolsterEntity> taunting, Target<BolsterEntity> taunted)
    {
        super(taunting);

        this.taunted = taunted;
    }

    @Override
    public void onActivate(Properties properties)
    {
        LivingEntity entity = taunted.get(properties).getBukkit();

        if (entity instanceof Creature)
        {
            ((Creature) entity).setTarget(this.getTarget().get(properties).getBukkit());
        }
    }
}
