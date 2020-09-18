package co.runed.bolster.abilities.conditions;

import co.runed.bolster.BolsterEntity;
import co.runed.bolster.abilities.Ability;
import co.runed.bolster.conditions.IConditional;
import co.runed.bolster.conditions.TargetedCondition;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import org.bukkit.entity.EntityType;

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
    public void onFail(IConditional conditional, Properties properties)
    {
        if (conditional instanceof Ability)
        {
            Ability ability = (Ability) conditional;

            if (ability.getTrigger().isPassive() || !(ability.shouldShowErrorMessages())) return;
        }

        if (!(conditional instanceof Ability)) return;

        BolsterEntity entity = this.getTarget().get(properties);

        if (entity.getType() == EntityType.PLAYER)
        {
            double cooldown = ((Ability) conditional).getRemainingCooldown();
            String formattedCooldown = "" + (int) cooldown;

            if (cooldown < 1)
            {
                formattedCooldown = decimalFormatter.format(cooldown);
            }

            entity.sendActionBar("Ability on cooldown (" + formattedCooldown + " seconds remaining)");
        }
    }
}
