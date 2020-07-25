package co.runed.bolster.abilities.conditions;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.abilities.PassiveAbility;
import co.runed.bolster.conditions.Condition;
import co.runed.bolster.conditions.IConditional;
import co.runed.bolster.properties.Properties;
import co.runed.bolster.util.PlayerUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class HasManaCondition extends Condition
{
    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        if (!(conditional instanceof Ability)) return false;

        LivingEntity entity = properties.get(AbilityProperties.CASTER);

        return Bolster.getManaManager().hasEnoughMana(entity, ((Ability) conditional).getManaCost());
    }

    @Override
    public void onFail(IConditional conditional, Properties properties)
    {
        if (conditional instanceof PassiveAbility) return;

        LivingEntity entity = properties.get(AbilityProperties.CASTER);

        if (entity.getType() == EntityType.PLAYER)
        {
            PlayerUtil.sendActionBar((Player) entity, ChatColor.LIGHT_PURPLE + "Not enough mana!");
        }
    }
}
