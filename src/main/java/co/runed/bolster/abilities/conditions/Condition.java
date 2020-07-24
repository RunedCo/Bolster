package co.runed.bolster.abilities.conditions;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.properties.Properties;

public abstract class Condition
{
    /**
     * Evaluate the outcome of a condition
     *
     * @param ability    the ability
     * @param properties the ability properties
     * @return true if the condition passes
     */
    public abstract boolean evaluate(Ability ability, Properties properties);

    /**
     * Callback for when a condition fails
     *
     * @param ability    the ability
     * @param properties the ability properties
     */
    public abstract void onFail(Ability ability, Properties properties);

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

