package co.runed.bolster.abilities.base;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.entity.Entity;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An ability that runs for multiple targets
 */
public class MultiTargetAbility extends MultiAbility
{
    Function<Properties, Collection<Entity>> entityFunction;

    public MultiTargetAbility(Function<Properties, Collection<Entity>> entityFunction)
    {
        this.setEntityFunction(entityFunction);
    }

    public void setEntityFunction(Function<Properties, Collection<Entity>> entityFunction)
    {
        this.entityFunction = entityFunction;
    }

    @Override
    public String getDescription()
    {
        if (super.getDescription() != null) return super.getDescription();

        String desc = "";

        for (Ability ability : this.getChildren())
        {
            if (ability.getDescription() == null) continue;

            desc += ability.getDescription() + "\n";
        }

        return desc;
    }

    // TODO might not work (see old implementation on github)
    @Override
    public void testActivate(Properties properties)
    {
        Collection<Entity> entities = entityFunction.apply(properties);

        for (Entity entity : entities)
        {
            Properties newProperties = new Properties(properties);
            newProperties.set(AbilityProperties.TARGET, entity);

            super.testActivate(newProperties);
        }
    }
}
