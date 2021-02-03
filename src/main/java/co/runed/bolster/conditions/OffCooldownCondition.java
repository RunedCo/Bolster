package co.runed.bolster.conditions;

import co.runed.bolster.BolsterEntity;
import co.runed.bolster.abilities.Ability;
import co.runed.bolster.util.cooldown.ICooldownSource;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import org.bukkit.ChatColor;

import java.text.DecimalFormat;

public class OffCooldownCondition extends TargetedCondition<BolsterEntity>
{
    private static final DecimalFormat decimalFormatter = new DecimalFormat("#.#");

    public OffCooldownCondition(Target<BolsterEntity> target)
    {
        super(target);
    }

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
    public void onFail(IConditional conditional, Properties properties, boolean inverted)
    {
        if (!conditional.shouldShowErrorMessages()) return;
        if (!(conditional instanceof ICooldownSource)) return;


    }

    @Override
    public String getErrorMessage(IConditional conditional, Properties properties, boolean inverted)
    {
        double cooldown = ((ICooldownSource) conditional).getRemainingCooldown();
        String formattedCooldown = "" + (int) cooldown;

        if (cooldown < 1)
        {
            formattedCooldown = decimalFormatter.format(cooldown);
        }

        if (inverted) return ChatColor.RED + "Ability is not on cooldown!";

        return ChatColor.RED + "Ability on cooldown (" + formattedCooldown + " seconds remaining)";
    }
}
