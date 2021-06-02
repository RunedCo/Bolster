package co.runed.bolster.abilities.base;

import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.abilities.core.SetPropertyAbility;

public class CancelEventAbility extends SetPropertyAbility<Boolean>
{
    public CancelEventAbility()
    {
        super(AbilityProperties.IS_CANCELLED, () -> true);
    }
}
