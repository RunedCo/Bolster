package co.runed.bolster.abilities.conditions;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.conditions.Condition;
import co.runed.bolster.conditions.IConditional;
import co.runed.bolster.properties.Properties;
import co.runed.bolster.status.StatusEffect;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

public class HasPotionEffect extends Condition
{
    PotionEffectType potionEffect;

    public HasPotionEffect(PotionEffectType potionEffect)
    {
        this.potionEffect = potionEffect;
    }

    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        LivingEntity caster = properties.get(AbilityProperties.CASTER);

        return caster.hasPotionEffect(potionEffect);
    }

    @Override
    public void onFail(IConditional conditional, Properties properties)
    {

    }
}