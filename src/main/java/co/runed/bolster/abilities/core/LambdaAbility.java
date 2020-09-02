package co.runed.bolster.abilities.core;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.entity.LivingEntity;

import java.util.function.BiConsumer;

public class LambdaAbility extends Ability
{
    BiConsumer<LivingEntity, Properties> lambda;

    public LambdaAbility(BiConsumer<LivingEntity, Properties> lambda)
    {
        this.lambda = lambda;
    }

    @Override
    public void onActivate(Properties properties)
    {
        this.lambda.accept(this.getCaster(), properties);
    }
}
