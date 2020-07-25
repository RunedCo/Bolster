package co.runed.bolster.conditions;

public enum ConditionPriority
{
    HIGHEST(4),
    HIGH(3),
    NORMAL(2),
    LOW(1),
    LOWEST(0);

    int priority;

    ConditionPriority(int priority)
    {
        this.priority = priority;
    }

    public int getPriority()
    {
        return priority;
    }
}
