package co.runed.bolster.abilities.targeted;

import co.runed.bolster.BolsterEntity;
import co.runed.bolster.abilities.TargetedAbility;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import org.bukkit.potion.PotionEffect;

import java.util.function.Supplier;

public class AddPotionEffectAbility extends TargetedAbility<BolsterEntity>
{
    Supplier<PotionEffect> effect;

    public AddPotionEffectAbility(Target<BolsterEntity> target, Supplier<PotionEffect> effect)
    {
        super(target);

        this.effect = effect;
    }

    @Override
    public void onActivate(Properties properties)
    {
        this.getTarget().get(properties).getBukkit().addPotionEffect(this.effect.get());
    }
}
