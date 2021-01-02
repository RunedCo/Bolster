package co.runed.bolster.conditions;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.util.properties.Properties;

public class AbilityInProgressCondition extends Condition
{
    Ability ability;

    public AbilityInProgressCondition(Ability ability)
    {
        super();

        this.ability = ability;
    }

    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        return this.ability != null && this.ability.isInProgress();
    }

    @Override
    public void onFail(IConditional conditional, Properties properties, boolean inverted)
    {

    }
}
