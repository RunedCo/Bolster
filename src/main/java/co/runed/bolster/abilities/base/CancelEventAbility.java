package co.runed.bolster.abilities.base;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public class CancelEventAbility extends Ability
{
    public CancelEventAbility()
    {
        super();
        //this.setShouldCancelEvent(true);
    }

    @Override
    public void onActivate(Properties properties)
    {
        if (properties.get(AbilityProperties.EVENT) != null)
        {
            Event event = properties.get(AbilityProperties.EVENT);

            if (event instanceof Cancellable)
            {
                ((Cancellable) event).setCancelled(true);
            }
        }

        properties.set(AbilityProperties.CANCELLED, true);
    }
}
