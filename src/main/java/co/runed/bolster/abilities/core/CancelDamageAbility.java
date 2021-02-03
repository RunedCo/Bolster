package co.runed.bolster.abilities.core;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;

public class CancelDamageAbility extends Ability
{
    public CancelDamageAbility()
    {
        super();
    }

    @Override
    public void onActivate(Properties properties)
    {
        if (properties.get(AbilityProperties.EVENT) != null)
        {
            Event event = properties.get(AbilityProperties.EVENT);

            if (event instanceof EntityDamageEvent)
            {
                EntityDamageEvent damageEvent = (EntityDamageEvent) event;

                if (damageEvent.getCause() == EntityDamageEvent.DamageCause.VOID) return;

                damageEvent.setDamage(0);
            }
        }
    }
}
