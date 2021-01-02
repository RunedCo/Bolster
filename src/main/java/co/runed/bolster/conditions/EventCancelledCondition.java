package co.runed.bolster.conditions;

import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.event.Cancellable;

public class EventCancelledCondition extends Condition
{
    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        if (!(properties.get(AbilityProperties.EVENT) instanceof Cancellable)) return false;

        return ((Cancellable) properties.get(AbilityProperties.EVENT)).isCancelled();
    }

    @Override
    public void onFail(IConditional conditional, Properties properties, boolean inverted)
    {

    }
}
