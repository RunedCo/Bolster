package co.runed.bolster.conditions;

import co.runed.bolster.util.properties.Properties;

import java.util.function.BiFunction;

public interface IConditional
{
    default void addCondition(Condition condition)
    {
        this.addCondition(condition, ConditionPriority.NORMAL);
    }

    default void addCondition(BiFunction<IConditional, Properties, Boolean> evaluateFunc)
    {
        this.addCondition(evaluateFunc, ConditionPriority.NORMAL);
    }

    default void addCondition(BiFunction<IConditional, Properties, Boolean> evaluateFunc, ConditionPriority priority)
    {
        this.addCondition(new LambdaCondition(evaluateFunc), priority);
    }

    void addCondition(Condition condition, ConditionPriority priority);
}
