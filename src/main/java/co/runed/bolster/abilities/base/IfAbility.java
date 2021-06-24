package co.runed.bolster.abilities.base;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.conditions.Condition;
import co.runed.bolster.util.properties.Properties;

public class IfAbility extends Ability
{
    Condition condition;
    Ability trueAbility;
    Ability falseAbility;

    public IfAbility(Condition condition, Ability trueAbility, Ability falseAbility)
    {
        super();

        this.condition = condition;
        this.trueAbility = trueAbility;
        this.falseAbility = falseAbility;
    }

    @Override
    public void onActivate(Properties properties)
    {
        if (this.condition.evaluate(this, properties))
        {
            this.trueAbility.activate(properties);
        }
        else
        {
            this.falseAbility.activate(properties);
        }
    }
}
