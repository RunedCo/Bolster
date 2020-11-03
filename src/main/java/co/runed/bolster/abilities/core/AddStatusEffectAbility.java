package co.runed.bolster.abilities.core;

import co.runed.bolster.BolsterEntity;
import co.runed.bolster.abilities.TargetedAbility;
import co.runed.bolster.status.StatusEffect;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import org.bukkit.entity.LivingEntity;

public class AddStatusEffectAbility extends TargetedAbility<BolsterEntity>
{
    StatusEffect statusEffect;

    public AddStatusEffectAbility(Target<BolsterEntity> target, StatusEffect statusEffect)
    {
        super(target);

        this.statusEffect = statusEffect;
    }

    @Override
    public void onActivate(Properties properties)
    {
        BolsterEntity.from(this.getCaster()).addStatusEffect(this.statusEffect);
    }
}
