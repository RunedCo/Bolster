package co.runed.bolster.conditions;

import co.runed.bolster.BolsterEntity;
import co.runed.bolster.classes.BolsterClass;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.registries.Registries;
import co.runed.bolster.util.target.Target;
import org.bukkit.ChatColor;
import org.bukkit.potion.PotionEffectType;

public class HasPotionEffect extends TargetedCondition<BolsterEntity>
{
    PotionEffectType potionEffect;

    public HasPotionEffect(Target<BolsterEntity> target, PotionEffectType potionEffect)
    {
        super(target);

        this.potionEffect = potionEffect;
    }

    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        return this.getTarget().get(properties).getBukkit().hasPotionEffect(potionEffect);
    }

    @Override
    public void onFail(IConditional conditional, Properties properties, boolean inverted)
    {

    }

    @Override
    public String getErrorMessage(IConditional conditional, Properties properties, boolean inverted)
    {
        if (inverted) return ChatColor.RED + "You must not have " + potionEffect.getName() + " to use this ability!";

        return ChatColor.RED + "You must have " + potionEffect.getName() + " to use this ability!";
    }
}