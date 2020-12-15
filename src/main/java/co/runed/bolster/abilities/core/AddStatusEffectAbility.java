package co.runed.bolster.abilities.core;

import co.runed.bolster.BolsterEntity;
import co.runed.bolster.abilities.TargetedAbility;
import co.runed.bolster.status.StatusEffect;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;

import java.util.function.Supplier;

public class AddStatusEffectAbility extends TargetedAbility<BolsterEntity>
{
    Supplier<StatusEffect> statusEffect;

    public AddStatusEffectAbility(Target<BolsterEntity> target, Supplier<StatusEffect> statusEffect)
    {
        super(target);

        this.statusEffect = statusEffect;
    }

    @Override
    public void onActivate(Properties properties)
    {
        BolsterEntity.from(this.getCaster()).addStatusEffect(this.statusEffect.get());
    }
}
