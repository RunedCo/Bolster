package co.runed.bolster.abilities.targeted;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.util.Operation;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;

public class ModifyDamageAbility extends Ability
{
    double amount;
    Operation operation;

    public ModifyDamageAbility(Operation operation, double amount)
    {
        super();

        this.amount = amount;
        this.operation = operation;
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

                double damage = damageEvent.getDamage();

                switch (this.operation)
                {
                    case ADD:
                        damage += this.amount;
                        break;
                    case SUBTRACT:
                        damage -= this.amount;
                        break;
                    case DIVIDE:
                        damage /= this.amount;
                        break;
                    case MULTIPLY:
                        damage *= this.amount;
                        break;
                    case SET:
                        damage = this.amount;
                        break;
                }

                damageEvent.setDamage(damage);

                // TODO check cancelled
                if (damageEvent.getFinalDamage() <= 0) properties.set(AbilityProperties.CANCELLED, true);
            }
        }
    }
}
