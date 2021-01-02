package co.runed.bolster.conditions;

import co.runed.bolster.util.properties.Properties;

public abstract class Condition
{
    /**
     * Evaluate the outcome of a condition
     *
     * @param conditional the conditional
     * @param properties  the conditional properties
     * @return true if the condition passes
     */
    public abstract boolean evaluate(IConditional conditional, Properties properties);

    /**
     * Callback for when a condition fails
     *  @param conditional the conditional
     * @param properties  the conditional properties
     * @param inverted
     */
    public abstract void onFail(IConditional conditional, Properties properties, boolean inverted);

    public static class Data implements Comparable<Data>
    {
        public Condition condition;
        public ConditionPriority priority;

        public Data(Condition condition, ConditionPriority priority)
        {
            this.condition = condition;
            this.priority = priority;
        }

        @Override
        public int compareTo(Data condition)
        {
            return this.priority.compareTo(condition.priority);
        }
    }
}

