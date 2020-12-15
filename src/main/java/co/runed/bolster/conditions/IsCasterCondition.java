package co.runed.bolster.conditions;

import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.BolsterEntity;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;

public class IsCasterCondition extends TargetedCondition<BolsterEntity>
{
    public IsCasterCondition(Target<BolsterEntity> target)
    {
        super(target);
    }

    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        return target.get(properties).equals(properties.get(AbilityProperties.CASTER));
    }

    @Override
    public void onFail(IConditional conditional, Properties properties)
    {

    }
}
