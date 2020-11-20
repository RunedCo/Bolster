package co.runed.bolster.conditions;

import co.runed.bolster.BolsterEntity;
import co.runed.bolster.abilities.Ability;
import co.runed.bolster.conditions.IConditional;
import co.runed.bolster.conditions.TargetedCondition;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

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
    public void onFail(IConditional conditional, Properties properties)
    {
        if (conditional instanceof Ability && ((Ability) conditional).getTrigger().isPassive()) return;

        BolsterEntity entity = this.getTarget().get(properties);

        if (entity.getType() == EntityType.PLAYER)
        {
            entity.sendActionBar(ChatColor.RED + "You are already full health!");
        }
    }
}
