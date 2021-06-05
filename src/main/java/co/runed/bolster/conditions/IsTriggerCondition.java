package co.runed.bolster.conditions;

import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.util.properties.Properties;

public class IsTriggerCondition extends Condition
{
    AbilityTrigger trigger;

    public IsTriggerCondition(AbilityTrigger trigger)
    {
        super();

        this.trigger = trigger;
    }

    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        return this.trigger.equals(properties.get(AbilityProperties.TRIGGER));
    }

    @Override
    public void onFail(IConditional conditional, Properties properties, boolean inverted)
    {

    }

    @Override
    public String getErrorMessage(IConditional conditional, Properties properties, boolean inverted)
    {
        return null;
    }
}
