package co.runed.bolster.abilities.conditions;

import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.conditions.Condition;
import co.runed.bolster.conditions.IConditional;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.entity.LivingEntity;

public class HasPermissionCondition extends Condition
{
    String permission;

    public HasPermissionCondition(String permission)
    {
        this.permission = permission;
    }

    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        LivingEntity entity = properties.get(AbilityProperties.CASTER);

        return entity.hasPermission(this.permission);
    }

    @Override
    public void onFail(IConditional conditional, Properties properties)
    {

    }
}
