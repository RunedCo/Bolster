package co.runed.bolster.abilities.conditions;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.abilities.PassiveAbility;
import co.runed.bolster.properties.Properties;
import co.runed.bolster.util.PlayerUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class HasManaCondition extends Condition
{
    @Override
    public boolean evaluate(Ability ability, Properties properties)
    {
        LivingEntity entity = properties.get(AbilityProperties.CASTER);

        return Bolster.getManaManager().getCurrentMana(entity) - ability.getManaCost() >= 0;
    }

    @Override
    public void onFail(Ability ability, Properties properties)
    {
        if (ability instanceof PassiveAbility) return;

        LivingEntity entity = properties.get(AbilityProperties.CASTER);

        if (entity.getType() == EntityType.PLAYER)
        {
            PlayerUtil.sendActionBar((Player) entity, ChatColor.LIGHT_PURPLE + "Not enough mana!");
        }
    }
}
