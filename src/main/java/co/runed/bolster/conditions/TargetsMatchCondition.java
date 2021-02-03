package co.runed.bolster.conditions;

import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;

public class TargetsMatchCondition<T> extends TargetedCondition<T>
{
    Target<T> target1;
    Target<T> target2;

    public TargetsMatchCondition(Target<T> target1, Target<T> target2)
    {
        super(target1);

        this.target1 = target1;
        this.target2 = target2;
    }

    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        return this.target1.get(properties).equals(this.target2.get(properties));
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
