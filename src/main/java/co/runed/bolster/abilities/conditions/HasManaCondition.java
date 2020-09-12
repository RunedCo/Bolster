package co.runed.bolster.abilities.conditions;

import co.runed.bolster.BolsterEntity;
import co.runed.bolster.abilities.Ability;
import co.runed.bolster.conditions.IConditional;
import co.runed.bolster.conditions.TargetedCondition;
import co.runed.bolster.managers.ManaManager;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;

public class HasManaCondition extends TargetedCondition<BolsterEntity>
{
    public HasManaCondition(Target<BolsterEntity> target)
    {
        super(target);
    }

    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        if (!(conditional instanceof Ability)) return false;

        BolsterEntity entity = this.getTarget().get(properties);

        return ManaManager.getInstance().hasEnoughMana(entity.getBukkit(), ((Ability) conditional).getManaCost());
    }

    @Override
    public void onFail(IConditional conditional, Properties properties)
    {
        if (conditional instanceof Ability && ((Ability) conditional).getTrigger().isPassive()) return;

        BolsterEntity entity = this.getTarget().get(properties);

        if (entity.getType() == EntityType.PLAYER)
        {
            entity.sendActionBar(ChatColor.LIGHT_PURPLE + "Not enough mana!");
        }
    }
}
