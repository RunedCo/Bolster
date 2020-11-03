package co.runed.bolster.abilities;

import co.runed.bolster.util.target.ITargeted;
import co.runed.bolster.util.target.Target;

public abstract class TargetedAbility<T> extends Ability implements ITargeted<T>
{
    Target<T> target;

    public TargetedAbility(Target<T> target)
    {
        super();

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
