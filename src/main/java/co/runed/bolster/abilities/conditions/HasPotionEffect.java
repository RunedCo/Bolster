package co.runed.bolster.abilities.conditions;

import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.conditions.IConditional;
import co.runed.bolster.conditions.TargetedCondition;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

public class HasPotionEffect extends TargetedCondition<LivingEntity>
{
    PotionEffectType potionEffect;

    public HasPotionEffect(Target<LivingEntity> target, PotionEffectType potionEffect)
    {
        super(target);

        this.potionEffect = potionEffect;
    }

    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        return this.getTarget().get(properties).hasPotionEffect(potionEffect);
    }

    @Override
    public void onFail(IConditional conditional, Properties properties)
    {

    }
}