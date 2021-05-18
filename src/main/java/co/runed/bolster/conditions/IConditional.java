package co.runed.bolster.conditions;

import co.runed.bolster.util.properties.Properties;

import java.util.function.BiFunction;

public interface IConditional<T>
{
    default T addCondition(Condition condition)
    {
        return this.addCondition(condition, ConditionPriority.NORMAL);
    }

    default T addCondition(BiFunction<IConditional, Properties, Boolean> evaluateFunc)
    {
        return this.addCondition(evaluateFunc, ConditionPriority.NORMAL);
    }

    default T addCondition(BiFunction<IConditional, Properties, Boolean> evaluateFunc, ConditionPriority priority)
    {
        return this.addCondition(new FunctionCondition(evaluateFunc), priority);
    }

    T addCondition(Condition condition, ConditionPriority priority);

    T setShouldShowErrorMessages(boolean showErrors);

    boolean shouldShowErrorMessages();
}
