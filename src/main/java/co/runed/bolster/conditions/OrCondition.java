package co.runed.bolster.conditions;

import co.runed.bolster.properties.Properties;

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
    public void onFail(IConditional conditional, Properties properties)
    {
        for (Condition condition : this.conditions)
        {
            condition.onFail(conditional, properties);
        }
    }
}
