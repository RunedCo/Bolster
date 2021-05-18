package co.runed.bolster.abilities.base;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.entity.LivingEntity;

import java.util.function.BiConsumer;

public class FunctionAbility extends Ability
{
    BiConsumer<LivingEntity, Properties> lambda;

    public FunctionAbility(BiConsumer<LivingEntity, Properties> lambda)
    {
        super();

        this.lambda = lambda;
    }

    @Override
    public void onActivate(Properties properties)
    {
        this.lambda.accept(this.getCaster(), properties);
    }
}
