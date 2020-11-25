package co.runed.bolster.conditions;

import co.runed.bolster.BolsterEntity;
import co.runed.bolster.status.StatusEffect;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.wip.target.Target;

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
