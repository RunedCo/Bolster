package co.runed.bolster.conditions;

import co.runed.bolster.BolsterEntity;
import co.runed.bolster.abilities.Ability;
import co.runed.bolster.managers.ManaManager;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;

public class HasManaCondition extends TargetedCondition<BolsterEntity>
{
    float amount = -1;

    public HasManaCondition(Target<BolsterEntity> target, float amount)
    {
        this(target);

        this.amount = amount;
    }

    public HasManaCondition(Target<BolsterEntity> target)
    {
        super(target);
    }

    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        if (!(conditional instanceof Ability)) return false;

        BolsterEntity entity = this.getTarget().get(properties);

        float reqAmount = amount > 0 ? amount : ((Ability) conditional).getManaCost();

        return ManaManager.getInstance().hasEnoughMana(entity.getBukkit(), reqAmount);
    }

    @Override
    public void onFail(IConditional conditional, Properties properties, boolean inverted)
    {
        if (conditional instanceof Ability && ((Ability) conditional).getTrigger().isPassive()) return;

        BolsterEntity entity = this.getTarget().get(properties);

        if (entity.getType() == EntityType.PLAYER)
        {
            entity.sendActionBar(ChatColor.LIGHT_PURPLE + "Not enough mana!");
        }
    }
}
