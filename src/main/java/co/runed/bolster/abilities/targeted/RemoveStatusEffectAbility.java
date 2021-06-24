package co.runed.bolster.abilities.targeted;

import co.runed.bolster.abilities.TargetedAbility;
import co.runed.bolster.entity.BolsterEntity;
import co.runed.bolster.status.StatusEffect;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;

public class RemoveStatusEffectAbility extends TargetedAbility<BolsterEntity>
{
    Class<? extends StatusEffect> statusEffect;

    public RemoveStatusEffectAbility(Target<BolsterEntity> target, Class<? extends StatusEffect> statusEffect)
    {
        super(target);

        this.statusEffect = statusEffect;
    }

    @Override
    public void onActivate(Properties properties)
    {
        BolsterEntity.from(this.getCaster()).clearStatusEffect(this.statusEffect);
    }
}
