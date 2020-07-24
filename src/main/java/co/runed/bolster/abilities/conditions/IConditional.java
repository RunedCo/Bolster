package co.runed.bolster.abilities.conditions;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.conditions.Condition;
import co.runed.bolster.abilities.conditions.ConditionPriority;

public interface IConditional
{
    default void addCondition(Condition condition)
    {
        this.addCondition(condition, true);
    }

    default void addCondition(Condition condition, boolean result)
    {
        this.addCondition(condition, result, ConditionPriority.NORMAL);
    }

    default void addCondition(Condition condition, ConditionPriority priority)
    {
        this.addCondition(condition, true, priority);
    }

    void addCondition(Condition condition, boolean result, ConditionPriority priority);
}
