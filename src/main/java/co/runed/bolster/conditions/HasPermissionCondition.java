package co.runed.bolster.conditions;

import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import org.bukkit.ChatColor;
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
    public void onFail(IConditional conditional, Properties properties, boolean inverted)
    {

    }

    @Override
    public String getErrorMessage(IConditional conditional, Properties properties, boolean inverted)
    {
        return ChatColor.RED + "You do not have permission to do that!";
    }
}
