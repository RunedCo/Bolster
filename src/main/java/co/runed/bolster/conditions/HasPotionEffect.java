package co.runed.bolster.conditions;

import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.wip.target.Target;
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