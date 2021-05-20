package co.runed.bolster.conditions;

import co.runed.bolster.BolsterEntity;
import co.runed.bolster.classes.BolsterClass;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;

public class IsAlliedCondition extends TargetedCondition<BolsterEntity>
{
    public IsAlliedCondition(Target<BolsterEntity> target)
    {
        super(target);
    }

    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        return false;
    }

    @Override
    public void onFail(IConditional conditional, Properties properties, boolean inverted)
    {

    }

    @Override
    public String getErrorMessage(IConditional conditional, Properties properties, boolean inverted)
    {
        return null;
    }
}
