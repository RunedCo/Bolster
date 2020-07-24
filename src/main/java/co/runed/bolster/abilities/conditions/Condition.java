package co.runed.bolster.abilities.conditions;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.properties.Properties;

public abstract class Condition
{
    public abstract boolean evaluate(Ability ability, Properties properties);

    public void onFail(Ability ability, Properties properties)
    {

    }

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

