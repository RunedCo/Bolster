package co.runed.bolster.conditions;

import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.conditions.base.Condition;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageCauseEqualsCondition extends Condition
{
    EntityDamageEvent.DamageCause damageCause;

    public DamageCauseEqualsCondition(EntityDamageEvent.DamageCause damageCause)
    {
        this.damageCause = damageCause;
    }

    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        if (!(properties.get(AbilityProperties.EVENT) instanceof EntityDamageEvent)) return false;

        return ((EntityDamageEvent) properties.get(AbilityProperties.EVENT)).getCause() == this.damageCause;
    }

    @Override
    public void onFail(IConditional conditional, Properties properties, boolean inverted)
    {

    }

    @Override
    public String getErrorMessage(IConditional conditional, Properties properties, boolean inverted)
    {
        return null;
    }
}
