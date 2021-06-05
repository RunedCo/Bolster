package co.runed.bolster.conditions;

import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.util.properties.Properties;

public class IsCancelledCondition extends Condition
{
    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        return properties.get(AbilityProperties.IS_CANCELLED);
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
