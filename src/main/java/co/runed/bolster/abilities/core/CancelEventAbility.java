package co.runed.bolster.abilities.core;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.util.properties.Properties;

public class CancelEventAbility extends Ability
{
    public CancelEventAbility()
    {
        super();

        this.setShouldCancelEvent(true);
    }

    @Override
    public void onActivate(Properties properties)
    {

    }
}
