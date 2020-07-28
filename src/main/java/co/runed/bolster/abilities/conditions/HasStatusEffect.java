package co.runed.bolster.abilities.conditions;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.conditions.Condition;
import co.runed.bolster.conditions.IConditional;
import co.runed.bolster.properties.Properties;
import co.runed.bolster.status.StatusEffect;
import org.bukkit.entity.LivingEntity;

public class HasStatusEffect extends Condition
{
    Class<? extends StatusEffect> statusEffect;

    public HasStatusEffect(Class<? extends StatusEffect> statusEffect)
    {
        this.statusEffect = statusEffect;
    }

    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        LivingEntity caster = properties.get(AbilityProperties.CASTER);

        return Bolster.getStatusEffectManager().hasStatusEffect(caster, this.statusEffect);
    }

    @Override
    public void onFail(IConditional conditional, Properties properties)
    {

    }
}
