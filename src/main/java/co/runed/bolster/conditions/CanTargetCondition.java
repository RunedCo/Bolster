package co.runed.bolster.conditions;

import co.runed.bolster.BolsterEntity;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;

public class CanTargetCondition extends TargetedCondition<BolsterEntity>
{
    TargetType type = TargetType.NEUTRAL;

    public CanTargetCondition(Target<BolsterEntity> target)
    {
        this(target, TargetType.NEUTRAL);
    }

    public CanTargetCondition(Target<BolsterEntity> target, TargetType type)
    {
        super(target);

        this.type = type;
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

    public enum TargetType
    {
        NEGATIVE,
        POSITIVE,
        NEUTRAL
    }
}
