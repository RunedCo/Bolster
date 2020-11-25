package co.runed.bolster.conditions;

import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.entity.EntityType;

public class IsEntityTypeCondition extends Condition
{
    EntityType type;

    public IsEntityTypeCondition(EntityType type)
    {
        this.type = type;
    }

    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        return properties.get(AbilityProperties.CASTER).getType() == EntityType.PLAYER;
    }

    @Override
    public void onFail(IConditional conditional, Properties properties)
    {

    }
}
