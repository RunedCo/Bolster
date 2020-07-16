package co.runed.bolster.abilities.conditions;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.properties.AbilityProperties;
import co.runed.bolster.properties.Properties;
import org.bukkit.entity.EntityType;

public class CasterIsEntityTypeCondition extends Condition
{
    EntityType type;

    public CasterIsEntityTypeCondition(EntityType type)
    {
        this.type = type;
    }

    @Override
    public boolean evaluate(Ability ability, Properties properties)
    {
        return properties.get(AbilityProperties.CASTER).getType() == EntityType.PLAYER;
    }
}
