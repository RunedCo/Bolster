package co.runed.bolster.conditions;

import co.runed.bolster.util.target.ITargeted;
import co.runed.bolster.util.target.Target;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public abstract class TargetedCondition<T> extends Condition
{
    Target<T> target;

    public TargetedCondition(Target<T> target)
    {
        this.target = target;
    }

    public Target<T> getTarget(IConditional conditional)
    {
        try
        {
            Target target = ((ITargeted) conditional).getTarget();
            return (Target<T>)target;
        }
        catch (Exception e)
        {
            return this.target;
        }
    }
}
