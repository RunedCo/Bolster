package co.runed.bolster.conditions;

import co.runed.bolster.util.target.Target;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.Location;

public class TargetedCondition extends Condition
{
    Target<Location> target;
    Condition condition;

    public TargetedCondition(Condition condition)
    {
        this(Target.CASTER_LOCATION, condition);
    }

    public TargetedCondition(Target<Location> target, Condition condition)
    {
        this.target = target;
        this.condition = condition;
    }

    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        properties.set(AbilityProperties.TARGET_LOCATION, this.target);

        return this.condition.evaluate(conditional, properties);
    }

    @Override
    public void onFail(IConditional conditional, Properties properties)
    {
        properties.set(AbilityProperties.TARGET_LOCATION, this.target);

        this.condition.onFail(conditional, properties);
    }
}
