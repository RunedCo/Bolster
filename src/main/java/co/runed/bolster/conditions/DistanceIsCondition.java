package co.runed.bolster.conditions;

import co.runed.bolster.util.Operator;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import org.bukkit.Location;

public class DistanceIsCondition extends TargetedCondition<Location>
{
    Target<Location> to;
    double value;
    Operator operator;

    public DistanceIsCondition(Target<Location> from, Target<Location> to, Operator operator, double value)
    {
        super(from);

        this.to = to;
        this.value = value;
        this.operator = operator;
    }

    private boolean checkDistance(Location from, Location to, Operator operator)
    {
        if (operator == Operator.EQUAL) return from.distance(to) == value;
        else if (operator == Operator.ABOVE) return from.distance(to) > value;
        else if (operator == Operator.BELOW) return from.distance(to) < value;
        return false;
    }

    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        Location from = this.getTarget().get(properties);
        Location to = this.to.get(properties);

        if (this.operator == Operator.ABOVE_OR_EQUAL)
            return checkDistance(from, to, Operator.ABOVE) || checkDistance(from, to, Operator.EQUAL);

        if (this.operator == Operator.BELOW_OR_EQUAL)
            return checkDistance(from, to, Operator.BELOW) || checkDistance(from, to, Operator.EQUAL);

        return checkDistance(from, to, this.operator);
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
