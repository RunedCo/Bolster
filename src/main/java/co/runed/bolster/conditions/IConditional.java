package co.runed.bolster.conditions;

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
