package co.runed.bolster.conditions;

import co.runed.bolster.util.properties.Properties;

public class NotCondition extends Condition
{
    Condition condition;

    public NotCondition(Condition condition)
    {
        this.condition = condition;
    }

    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        return !this.condition.evaluate(conditional, properties);
    }

    @Override
    public void onFail(IConditional conditional, Properties properties, boolean inverted)
    {
        this.condition.onFail(conditional, properties, !inverted);
    }
}