package co.runed.bolster.abilities.base;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.conditions.base.Condition;
import co.runed.bolster.util.properties.Properties;

public class IfAbility extends Ability
{
    Condition condition;
    Ability trueAbility;
    Ability falseAbility;

    public IfAbility(Condition condition)
    {
        super();

        this.condition = condition;
    }

    public IfAbility(Condition condition, Ability trueAbility, Ability falseAbility)
    {
        this(condition);

        this.trueAbility = trueAbility;
        this.falseAbility = falseAbility;
    }

    public IfAbility thenDo(Ability ability)
    {
        this.trueAbility = ability;

        return this;
    }

    public IfAbility elseDo(Ability ability)
    {
        this.falseAbility = ability;

        return this;
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
