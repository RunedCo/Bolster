package co.runed.bolster.abilities;

import co.runed.bolster.conditions.Condition;
import co.runed.bolster.conditions.ConditionPriority;
import co.runed.bolster.util.cost.Cost;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.entity.LivingEntity;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class AbilityBuilder
{
    List<Ability> abilities = new ArrayList<>();
    List<Condition.Data> conditions = new ArrayList<>();
    List<Cost> costs = new ArrayList<>();

    String description = null;
    double cooldown = 0;
    int manaCost = 0;
    Duration duration = null;

    public AbilityBuilder setDescription(String description)
    {
        this.description = description;

        return this;
    }

    public AbilityBuilder setCooldown(double cooldown)
    {
        this.cooldown = cooldown;

        return this;
    }

    public AbilityBuilder setManaCost(int manaCost)
    {
        this.manaCost = manaCost;

        return this;
    }

    public AbilityBuilder setDuration(Duration duration)
    {
        this.duration = duration;

        return this;
    }

    public AbilityBuilder addCost(Cost cost)
    {
        this.costs.add(cost);

        return this;
    }

    public AbilityBuilder addCondition(Condition condition, ConditionPriority conditionPriority)
    {
        this.conditions.add(new Condition.Data(condition, conditionPriority));

        return this;
    }

    public AbilityBuilder addAbility(BiConsumer<LivingEntity, Properties> lambda)
    {
        this.addAbility(new LambdaAbility(lambda));

        return this;
    }

    public AbilityBuilder addAbility(Ability ability)
    {
        this.abilities.add(ability);

        return this;
    }

    public Ability build()
    {
        MultiAbility ability = new MultiAbility();;

        for (Ability a : this.abilities)
        {
            ability.addAbility(a);
        }

        for (Condition.Data c : this.conditions)
        {
            ability.addCondition(c.condition, c.priority);
        }

        for (Cost c : this.costs)
        {
            ability.addCost(c);
        }

        if (this.description != null) ability.setDescription(description);

        ability.setCooldown(cooldown);
        ability.setManaCost(manaCost);
        ability.setDuration(duration);

        return ability;
    }
}
