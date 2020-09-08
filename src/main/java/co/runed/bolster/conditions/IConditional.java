package co.runed.bolster.conditions;

public interface IConditional
{
    default void addCondition(Condition condition)
    {
        this.addCondition(condition, ConditionPriority.NORMAL);
    }

    void addCondition(Condition condition, ConditionPriority priority);
}
