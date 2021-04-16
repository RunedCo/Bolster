package co.runed.bolster.abilities.targeted;

import co.runed.bolster.BolsterEntity;
import co.runed.bolster.abilities.TargetedAbility;
import co.runed.bolster.status.StatusEffect;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;

public class ClearStatusEffectAbility extends TargetedAbility<BolsterEntity>
{
    Class<? extends StatusEffect> statusEffect;

    public ClearStatusEffectAbility(Target<BolsterEntity> target, Class<? extends StatusEffect> statusEffect)
    {
        super(target);

        this.statusEffect = statusEffect;
    }

    @Override
    public void onActivate(Properties properties)
    {
        this.getTarget().get(properties).clearStatusEffect(statusEffect);
    }
}
