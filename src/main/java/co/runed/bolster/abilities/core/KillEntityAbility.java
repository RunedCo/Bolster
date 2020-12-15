package co.runed.bolster.abilities.core;

import co.runed.bolster.abilities.TargetedAbility;
import co.runed.bolster.BolsterEntity;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.v1_16_R3.CraftUtil;
import co.runed.bolster.util.target.Target;

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

        CraftUtil.killEntity(entity.getBukkit());
    }
}
