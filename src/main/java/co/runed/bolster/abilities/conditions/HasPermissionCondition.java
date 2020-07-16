package co.runed.bolster.abilities.conditions;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.properties.AbilityProperties;
import co.runed.bolster.properties.Properties;
import org.bukkit.entity.LivingEntity;

public class HasPermissionCondition extends Condition
{
    String permission;

    public HasPermissionCondition(String permission)
    {
        this.permission = permission;
    }

    @Override
    public boolean evaluate(Ability ability, Properties properties)
    {
        LivingEntity entity = properties.get(AbilityProperties.CASTER);

        return entity.hasPermission(this.permission);
    }
}
