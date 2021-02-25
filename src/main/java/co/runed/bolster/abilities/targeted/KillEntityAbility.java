package co.runed.bolster.abilities.targeted;

import co.runed.bolster.BolsterEntity;
import co.runed.bolster.abilities.TargetedAbility;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import org.bukkit.entity.ArmorStand;

public class KillEntityAbility extends TargetedAbility<BolsterEntity>
{
    public KillEntityAbility(Target<BolsterEntity> target)
    {
        super(target);
    }

    @Override
    public void onActivate(Properties properties)
    {
        BolsterEntity entity = this.getTarget().get(properties);

        if (entity.getBukkit() instanceof ArmorStand)
        {
            entity.getBukkit().setHealth(0);
        }
        else
        {
            entity.getBukkit().damage(1000);
        }
    }
}
