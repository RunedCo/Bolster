package co.runed.bolster.abilities.base;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityProvider;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.util.properties.Properties;

public class ForwardTriggerAbility extends Ability
{
    AbilityTrigger trigger;
    boolean limitToParent = false;

    public ForwardTriggerAbility(AbilityTrigger trigger)
    {
        this(trigger, false);
    }

    public ForwardTriggerAbility(AbilityTrigger trigger, boolean limitToParent)
    {
        super();

        this.trigger = trigger;
        this.limitToParent = limitToParent;
    }

    @Override
    public void onActivate(Properties properties)
    {
        AbilityProvider parent = this.limitToParent ? this.getAbilityProvider() : null;

        this.trigger.trigger(this.getCaster(), parent, properties);
    }
}
