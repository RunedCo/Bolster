package co.runed.bolster.conditions;

import co.runed.bolster.BolsterEntity;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import org.bukkit.ChatColor;

public class IsMaxHealthCondition extends TargetedCondition<BolsterEntity>
{
    public IsMaxHealthCondition(Target<BolsterEntity> target)
    {
        super(target);
    }

    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        BolsterEntity entity = this.getTarget().get(properties);
        double maxHealth = entity.getMaxHealth();

        return entity.getHealth() >= maxHealth;
    }

    @Override
    public void onFail(IConditional conditional, Properties properties, boolean inverted)
    {

    }

    @Override
    public String getErrorMessage(IConditional conditional, Properties properties, boolean inverted)
    {
        if (inverted) return ChatColor.RED + "You are already at full health!";

        return ChatColor.RED + "You are not at full health!";
    }
}
