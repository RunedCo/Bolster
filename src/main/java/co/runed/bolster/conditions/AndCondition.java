package co.runed.bolster.conditions;

import co.runed.bolster.util.properties.Properties;

import java.util.List;

public class AndCondition extends Condition
{
    Condition[] conditions;

    public AndCondition(List<Condition> conditions)
    {
        this(conditions.toArray(new Condition[0]));
    }

    public AndCondition(Condition... conditions)
    {
        this.conditions = conditions;
    }

    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        for (Condition condition : this.conditions)
        {
            if (!condition.evaluate(conditional, properties))
            {
                condition.onFail(conditional, properties, false);
                return false;
            }
        }

        return true;
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
