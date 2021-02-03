package co.runed.bolster.conditions;

import co.runed.bolster.util.properties.Properties;

import java.util.function.BiFunction;

public class LambdaCondition extends Condition
{
    BiFunction<IConditional, Properties, Boolean> evaluateFunc;

    public LambdaCondition(BiFunction<IConditional, Properties, Boolean> evaluateFunc)
    {
        super();

        this.evaluateFunc = evaluateFunc;
    }

    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        return this.evaluateFunc.apply(conditional, properties);
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
