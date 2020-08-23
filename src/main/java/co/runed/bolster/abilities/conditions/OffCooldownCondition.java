package co.runed.bolster.abilities.conditions;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.conditions.Condition;
import co.runed.bolster.conditions.IConditional;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.PlayerUtil;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class OffCooldownCondition extends Condition
{
    private static final DecimalFormat decimalFormatter = new DecimalFormat("#.#");

    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        if (conditional instanceof Ability)
        {
            return !((Ability) conditional).isOnCooldown();
        }

        return false;
    }

    //TODO: REMOVE HARDCODED MESSAGE
    @Override
    public void onFail(IConditional conditional, Properties properties)
    {
        if (conditional instanceof Ability && ((Ability) conditional).getTrigger() == AbilityTrigger.TICK) return;
        if (!(conditional instanceof Ability)) return;

        LivingEntity entity = properties.get(AbilityProperties.CASTER);

        if (entity.getType() == EntityType.PLAYER)
        {
            double cooldown = ((Ability) conditional).getRemainingCooldown();
            String formattedCooldown = "" + (int) cooldown;

            if (cooldown < 1)
            {
                formattedCooldown = decimalFormatter.format(cooldown);
            }

            PlayerUtil.sendActionBar((Player) entity, "Ability on cooldown (" + formattedCooldown + " seconds remaining)");
        }
    }
}
