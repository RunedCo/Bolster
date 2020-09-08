package co.runed.bolster.abilities.conditions;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.conditions.Condition;
import co.runed.bolster.conditions.IConditional;
import co.runed.bolster.conditions.TargetedCondition;
import co.runed.bolster.game.BolsterEntity;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.status.StatusEffect;
import co.runed.bolster.util.target.Target;
import org.bukkit.entity.LivingEntity;

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
    public void onFail(IConditional conditional, Properties properties)
    {

    }
}
