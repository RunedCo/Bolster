package co.runed.bolster.conditions.base;

import co.runed.bolster.conditions.IConditional;
import co.runed.bolster.util.properties.Properties;

import java.util.List;

public class OrCondition extends Condition
{
    Condition[] conditions;

    public OrCondition(List<Condition> conditions)
    {
        this(conditions.toArray(new Condition[0]));
    }

    public OrCondition(Condition... conditions)
    {
        this.conditions = conditions;
    }

    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        for (Condition condition : this.conditions)
        {
            if (condition.evaluate(conditional, properties))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onFail(IConditional conditional, Properties properties, boolean inverted)
    {
        for (Condition condition : this.conditions)
        {
            condition.onFail(conditional, properties, inverted);
        }
    }

    // todo check which conditions failed and then run error message only for that condition
    @Override
    public String getErrorMessage(IConditional conditional, Properties properties, boolean inverted)
    {
        return null;
    }
}
