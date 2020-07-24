package co.runed.bolster.abilities.conditions;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.properties.Properties;
import org.bukkit.entity.EntityType;

public class IsEntityTypeCondition extends Condition
{
    EntityType type;

    public IsEntityTypeCondition(EntityType type)
    {
        this.type = type;
    }

    @Override
    public boolean evaluate(Ability ability, Properties properties)
    {
        return properties.get(AbilityProperties.CASTER).getType() == EntityType.PLAYER;
    }

    @Override
    public void onFail(Ability ability, Properties properties)
    {

    }
}
