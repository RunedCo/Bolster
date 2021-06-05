package co.runed.bolster.abilities.base;

import co.runed.bolster.BolsterEntity;
import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.conditions.Condition;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * An ability that runs for multiple targets
 */
public class MultiTargetAbility extends MultiAbility
{
    Function<Properties, List<Entity>> entityFunction;
    int maxTargets = -1; // -1 is no limit
    List<Target<BolsterEntity>> ignoredTargets = new ArrayList<>();
    boolean shouldActivateIfEmpty = false;

    private boolean lastRunEmpty;
    private List<Condition.Data> parentConditions = new ArrayList<>();
    private boolean addConditionsToParent = true;

    public MultiTargetAbility(Function<Properties, List<Entity>> entityFunction)
    {
        super();

        this.setEntityFunction(entityFunction);

//        this.setEvaluateConditions(false);

        this.moveDefaultConditions();
    }

    private void moveDefaultConditions()
    {
        this.parentConditions = new ArrayList<>(this.conditions);
        this.conditions.clear();
    }

    public MultiTargetAbility setEntityFunction(Function<Properties, List<Entity>> entityFunction)
    {
        this.entityFunction = entityFunction;

        return this;
    }

    public MultiTargetAbility setMaxTargets(int maxTargets)
    {
        this.maxTargets = maxTargets;

        return this;
    }

    public MultiTargetAbility addIgnoredTarget(Target<BolsterEntity> target)
    {
        this.ignoredTargets.add(target);

        return this;
    }

    public MultiTargetAbility setShouldActivateIfEmpty(boolean shouldActivateIfEmpty)
    {
        this.shouldActivateIfEmpty = shouldActivateIfEmpty;

        return this;
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

    @Override
    public boolean evaluateConditions(Properties properties)
    {
        // COPY OLD CONDITIONS
        List<Condition.Data> oldConditions = new ArrayList<>(this.conditions);

        // REPLACE OLD CONDITIONS WITH PARENT CONDITIONS
        this.conditions = new ArrayList<>(this.parentConditions);

        // EVALUATE OLD + PARENT
        boolean success = super.evaluateConditions(properties);

        // REVERT TO OLD CONDITIONS
        this.conditions = oldConditions;

        return success;
    }

    // TODO might not work (see old implementation on github)
    @Override
    public void activateAbilityAndChildren(Properties properties)
    {
        List<Entity> entities = entityFunction.apply(properties);

        for (Target<BolsterEntity> ignored : this.ignoredTargets)
        {
            entities.remove(ignored.get(properties).getBukkit());
        }

        int count = Math.min(maxTargets > 0 ? maxTargets : entities.size(), entities.size());

        lastRunEmpty = entities.size() <= 0;

        for (int i = 0; i < count; i++)
        {
            Entity entity = entities.get(i);
            Properties newProperties = new Properties(properties);
            newProperties.set(AbilityProperties.TARGET, entity);

            if (super.canActivate(newProperties) && super.evaluateConditions(newProperties))
            {
                super.activateAbilityAndChildren(newProperties);
            }
        }
    }

    @Override
    public void onPostActivate(Properties properties)
    {
        if (!lastRunEmpty || shouldActivateIfEmpty) super.onPostActivate(properties);
    }
}
