package co.runed.bolster.abilities.core;

import co.runed.bolster.entity.BolsterEntity;
import co.runed.bolster.abilities.TargetedAbility;
import co.runed.bolster.conditions.IsMaxHealthCondition;
import co.runed.bolster.conditions.NotCondition;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.wip.target.Target;

public class HealAbility extends TargetedAbility<BolsterEntity>
{
    double healAmount;

    public HealAbility(Target<BolsterEntity> target)
    {
        this(target, -1);
    }

    public HealAbility(Target<BolsterEntity> target, double healAmount)
    {
        super(target);

        this.healAmount = healAmount;

        this.addCondition(new NotCondition(new IsMaxHealthCondition(target)));
    }

    @Override
    public void onActivate(Properties properties)
    {
        BolsterEntity bolsterEntity = BolsterEntity.from(this.getCaster());

        bolsterEntity.addHealth(this.healAmount);
    }
}
