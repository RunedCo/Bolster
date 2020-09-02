package co.runed.bolster.abilities.conditions;

import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.conditions.Condition;
import co.runed.bolster.conditions.IConditional;
import co.runed.bolster.conditions.TargetedCondition;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import org.bukkit.Location;

public class LightLevelCondition extends TargetedCondition<Location>
{
    int level;
    Operator operator;

    public LightLevelCondition(Target<Location> target, int level, Operator operator)
    {
        super(target);

        this.level = level;
        this.operator = operator;
    }

    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        Location location = this.getTarget().get(properties);
        return lightLevel(location);
    }

    @Override
    public void onFail(IConditional conditional, Properties properties)
    {

    }

    private boolean lightLevel(Location location) {
        if (operator == Operator.EQUAL) return location.getBlock().getLightLevel() == level;
        else if (operator == Operator.ABOVE) return location.getBlock().getLightLevel() > level;
        else if (operator == Operator.BELOW) return location.getBlock().getLightLevel() < level;
        return false;
    }

    public enum Operator
    {
        ABOVE,
        EQUAL,
        BELOW
    }
}
