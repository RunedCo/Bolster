package co.runed.bolster.conditions;

import co.runed.bolster.entity.BolsterEntity;
import co.runed.bolster.status.StatusEffect;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.registries.Registries;
import co.runed.bolster.util.target.Target;
import org.bukkit.ChatColor;

public class HasStatusEffect extends TargetedCondition<BolsterEntity>
{
    Class<? extends StatusEffect> statusEffect;

    public HasStatusEffect(Target<BolsterEntity> target, Class<? extends StatusEffect> statusEffect)
    {
        super(target);

        this.statusEffect = statusEffect;
    }

    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        return this.getTarget().get(properties).hasStatusEffect(this.statusEffect);
    }

    @Override
    public void onFail(IConditional conditional, Properties properties, boolean inverted)
    {

    }

    @Override
    public String getErrorMessage(IConditional conditional, Properties properties, boolean inverted)
    {
        StatusEffect status = Registries.STATUS_EFFECTS.get(this.statusEffect);
        if (status == null || status.getName() == null) return null;

        if (inverted) return ChatColor.RED + "You must not have " + status.getName() + " to use this ability!";

        return ChatColor.RED + "You must have " + status.getName() + " to use this ability!";
    }
}
