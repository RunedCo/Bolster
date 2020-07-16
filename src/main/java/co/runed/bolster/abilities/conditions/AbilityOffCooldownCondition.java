package co.runed.bolster.abilities.conditions;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.PassiveAbility;
import co.runed.bolster.abilities.properties.AbilityProperties;
import co.runed.bolster.properties.Properties;
import co.runed.bolster.util.PlayerUtil;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class AbilityOffCooldownCondition extends Condition
{
    private static DecimalFormat df2 = new DecimalFormat("#.#");

    @Override
    public boolean evaluate(Ability ability, Properties properties)
    {
        return !ability.isOnCooldown();
    }

    //TODO: REMOVE HARDCODED MESSAGE
    @Override
    public void onFail(Ability ability, Properties properties)
    {
        if (ability instanceof PassiveAbility) return;

        LivingEntity entity = properties.get(AbilityProperties.CASTER);

        if (entity.getType() == EntityType.PLAYER)
        {
            double cooldown = ability.getRemainingCooldown();
            String formattedCooldown = "" + (int) cooldown;

            if (cooldown < 1)
            {
                formattedCooldown = df2.format(cooldown);
            }

            PlayerUtil.sendActionBar((Player) entity, "Ability on cooldown (" + formattedCooldown + " seconds remaining)");
        }
        //player.sendMessage("Ability on cooldown (" + ability.getRemainingCooldown() +" seconds remaining)");
    }
}
