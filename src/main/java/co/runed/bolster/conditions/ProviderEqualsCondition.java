package co.runed.bolster.conditions;

import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.abilities.AbilityProvider;
import co.runed.bolster.conditions.base.Condition;
import co.runed.bolster.util.properties.Properties;

public class ProviderEqualsCondition extends Condition
{
    AbilityProvider provider;

    public ProviderEqualsCondition(AbilityProvider provider)
    {
        super();

        this.provider = provider;
    }

    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        return this.provider.equals(properties.get(AbilityProperties.ABILITY_PROVIDER));
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
