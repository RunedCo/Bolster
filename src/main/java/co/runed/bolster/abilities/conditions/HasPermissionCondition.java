package co.runed.bolster.abilities.conditions;

import co.runed.bolster.conditions.IConditional;
import co.runed.bolster.conditions.TargetedCondition;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import org.bukkit.entity.LivingEntity;

public class HasPermissionCondition extends TargetedCondition<LivingEntity>
{
    String permission;

    public HasPermissionCondition(Target<LivingEntity> target, String permission)
    {
        super(target);

        this.permission = permission;
    }

    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        return this.getTarget().get(properties).hasPermission(this.permission);
    }

    @Override
    public void onFail(IConditional conditional, Properties properties)
    {

    }
}
