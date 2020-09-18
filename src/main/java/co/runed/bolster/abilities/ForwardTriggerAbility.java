package co.runed.bolster.abilities;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.util.properties.Properties;

public class ForwardTriggerAbility extends Ability
{
    AbilityTrigger trigger;

    public ForwardTriggerAbility(AbilityTrigger trigger)
    {
        this.trigger = trigger;
    }

    @Override
    public void onActivate(Properties properties)
    {
        this.trigger.trigger(this.getCaster(), properties);
    }
}
