package co.runed.bolster.conditions;

import co.runed.bolster.util.target.ITargeted;
import co.runed.bolster.util.target.Target;

@Deprecated
public abstract class TargetedCondition<T> extends Condition implements ITargeted<T>
{
    Target<T> target;

    public TargetedCondition(Target<T> target)
    {
        this.target = target;
    }

    @Override
    public Target<T> getTarget()
    {
        return this.target;
    }

    @Override
    public void setTarget(Target<T> target)
    {
        this.target = target;
    }
}
