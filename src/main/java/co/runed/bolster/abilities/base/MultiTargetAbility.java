package co.runed.bolster.abilities.base;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.entity.Entity;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * An ability that runs for multiple targets
 */
public class MultiTargetAbility extends MultiAbility
{
    Function<Properties, List<Entity>> entityFunction;
    int maxTargets = -1;

    public MultiTargetAbility(Function<Properties, List<Entity>> entityFunction)
    {
        this.setEntityFunction(entityFunction);

        this.setEvaluateConditions(false);
    }

    public void setEntityFunction(Function<Properties, List<Entity>> entityFunction)
    {
        this.entityFunction = entityFunction;
    }

    public void setMaxTargets(int maxTargets)
    {
        this.maxTargets = maxTargets;
    }

    @Override
    public String getDescription()
    {
        if (super.getDescription() != null) return super.getDescription();

        String desc = "";

        for (Ability ability : this.getChildren())
        {
            if (ability.getDescription() == null || ability.getDescription().isEmpty()) continue;

            desc += ability.getDescription() + "\n";
        }

        return desc;
    }

    // TODO might not work (see old implementation on github)
    @Override
    public void activateAbilityAndChildren(Properties properties)
    {
        List<Entity> entities = entityFunction.apply(properties);

        int count = maxTargets > 0 ? maxTargets : entities.size();

        for (int i = 0; i < count; i++)
        {
            Entity entity = entities.get(i);
            Properties newProperties = new Properties(properties);
            newProperties.set(AbilityProperties.TARGET, entity);

            if (this.canActivate(newProperties) && this.evaluateConditions(newProperties))
            {
                super.activateAbilityAndChildren(newProperties);
            }
        }
    }
}
