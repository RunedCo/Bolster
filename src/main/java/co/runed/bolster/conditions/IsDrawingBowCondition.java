package co.runed.bolster.conditions;

import co.runed.bolster.entity.BolsterEntity;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;

public class IsDrawingBowCondition extends TargetedCondition<BolsterEntity>
{
    public IsDrawingBowCondition(Target<BolsterEntity> target)
    {
        super(target);
    }

    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        return this.getTarget().get(properties).isDrawingBow();
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
