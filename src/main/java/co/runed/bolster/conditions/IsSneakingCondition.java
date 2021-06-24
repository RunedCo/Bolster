package co.runed.bolster.conditions;

import co.runed.bolster.entity.BolsterEntity;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class IsSneakingCondition extends TargetedCondition<BolsterEntity>
{
    public IsSneakingCondition(Target<BolsterEntity> target)
    {
        super(target);
    }

    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        LivingEntity entity = this.getTarget().get(properties).getBukkit();

        return entity instanceof Player && ((Player) entity).isSneaking();
    }

    @Override
    public void onFail(IConditional conditional, Properties properties, boolean inverted)
    {

    }

    @Override
    public String getErrorMessage(IConditional conditional, Properties properties, boolean inverted)
    {
        if (inverted) return ChatColor.RED + "You must not be sneaking to use this ability!";

        return ChatColor.RED + "You must be sneaking to use this ability!";
    }
}
