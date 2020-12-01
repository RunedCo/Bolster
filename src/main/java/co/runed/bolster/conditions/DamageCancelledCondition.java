package co.runed.bolster.conditions;

import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.conditions.Condition;
import co.runed.bolster.conditions.IConditional;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageCancelledCondition extends Condition
{
    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        if (!(properties.get(AbilityProperties.EVENT) instanceof EntityDamageEvent)) return false;

        return ((EntityDamageEvent) properties.get(AbilityProperties.EVENT)).getFinalDamage() <= 0;
    }

    @Override
    public void onFail(IConditional conditional, Properties properties)
    {

    }
}
