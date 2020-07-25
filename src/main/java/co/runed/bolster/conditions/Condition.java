package co.runed.bolster.conditions;

import co.runed.bolster.properties.Properties;

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
     *
     * @param conditional the conditional
     * @param properties  the conditional properties
     */
    public abstract void onFail(IConditional conditional, Properties properties);

    public static class Data implements Comparable<Data>
    {
        public Condition condition;
        public boolean result;
        public ConditionPriority priority;

        public Data(Condition condition, boolean result, ConditionPriority priority)
        {
            this.condition = condition;
            this.result = result;
            this.priority = priority;
        }

        @Override
        public int compareTo(Data condition)
        {
            return this.priority.compareTo(condition.priority);
        }
    }
}

