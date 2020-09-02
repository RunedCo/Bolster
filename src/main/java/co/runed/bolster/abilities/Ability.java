package co.runed.bolster.abilities;

import co.runed.bolster.Bolster;
import co.runed.bolster.conditions.IConditional;
import co.runed.bolster.abilities.conditions.OffCooldownCondition;
import co.runed.bolster.conditions.Condition;
import co.runed.bolster.conditions.ConditionPriority;
import co.runed.bolster.abilities.conditions.HasManaCondition;
import co.runed.bolster.abilities.costs.AbilityCost;
import co.runed.bolster.abilities.costs.ManaAbilityCost;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.time.Duration;
import java.util.*;

public abstract class Ability implements Listener, IConditional, ICooldownSource
{
    private final String id = UUID.randomUUID().toString();
    private String description;
    private double cooldown = 0;
    private float manaCost = 0;
    private Boolean cancelEventOnCast = false;
    private LivingEntity caster;
    private AbilityProvider abilityProvider;
    private AbilityTrigger trigger;

    private final List<Condition.Data> conditions = new ArrayList<>();
    private final List<AbilityCost> costs = new ArrayList<>();

    public Ability()
    {
        Bukkit.getPluginManager().registerEvents(this, Bolster.getInstance());

        this.addCost(new ManaAbilityCost(this.getManaCost()));

        this.addCondition(new OffCooldownCondition(Target.CASTER), ConditionPriority.LOWEST);
        this.addCondition(new HasManaCondition(Target.CASTER), ConditionPriority.LOWEST);
    }

    public String getId()
    {
        return this.getAbilityProvider().getId() + "." + this.id;
    }

    public String getDescription()
    {
        return this.description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public LivingEntity getCaster()
    {
        return this.caster;
    }

    public void setCaster(LivingEntity caster)
    {
        this.caster = caster;
    }

    public float getManaCost()
    {
        return this.manaCost;
    }

    public void setManaCost(float manaCost)
    {
        this.manaCost = manaCost;
    }

    public void addCost(AbilityCost cost)
    {
        this.costs.add(cost);
    }

    public AbilityTrigger getTrigger()
    {
        return this.trigger;
    }

    public void setTrigger(AbilityTrigger trigger)
    {
        this.trigger = trigger;
    }

    @Override
    public void addCondition(Condition condition, boolean result, ConditionPriority priority)
    {
        this.conditions.add(new Condition.Data(condition, result, priority));
    }

    public Duration getDuration()
    {
        return Duration.ZERO;
    }

    public boolean shouldCancelEvent()
    {
        return cancelEventOnCast;
    }

    public void setShouldCancelEvent(boolean cancelEventOnCast)
    {
        this.cancelEventOnCast = cancelEventOnCast;
    }

    @Override
    public double getCooldown() {
        return this.cooldown;
    }

    @Override
    public void setCooldown(double cooldown)
    {
        this.cooldown = cooldown;
    }

    @Override
    public double getRemainingCooldown()
    {
        return Bolster.getCooldownManager().getRemainingTime(this.getCaster(), this);
    }

    @Override
    public void setOnCooldown(boolean onCooldown)
    {
        Bolster.getCooldownManager().setCooldown(this.getCaster(), this, this.getCooldown());
    }

    @Override
    public boolean isOnCooldown()
    {
        return this.getRemainingCooldown() > 0;
    }

    @Override
    public void clearCooldown()
    {
        Bolster.getCooldownManager().clearCooldown(this.getCaster(), this);
    }

    public AbilityProvider getAbilityProvider()
    {
        return this.abilityProvider;
    }

    public void setAbilityProvider(AbilityProvider abilityProvider)
    {
        this.abilityProvider = abilityProvider;
    }

    public boolean canActivate(Properties properties)
    {
        if (!properties.contains(AbilityProperties.CASTER)) return false;

        Collections.sort(this.conditions);

        for (Condition.Data data : this.conditions)
        {
            Condition condition = data.condition;

            boolean result = condition.evaluate(this, properties);

            if (result != data.result)
            {
                condition.onFail(this, properties);

                return false;
            }
        }

        // loop through every cost and remove
        for (AbilityCost cost : this.costs)
        {
            boolean result = cost.run(this, properties);

            if (!result)
            {
                return false;
            }
        }

        return true;
    }

    public boolean activate(Properties properties)
    {
        if (this.canActivate(properties))
        {
            this.onActivate(properties);

            this.onPostActivate(properties);

            return true;
        }

        return false;
    }

    public abstract void onActivate(Properties properties);

    public void onPostActivate(Properties properties)
    {
        this.setOnCooldown(true);

        if (properties.get(AbilityProperties.EVENT) != null)
        {
            Event event = properties.get(AbilityProperties.EVENT);

            if (event instanceof Cancellable && this.shouldCancelEvent())
            {
                ((Cancellable) event).setCancelled(true);
            }
        }
    }

    public void destroy()
    {
        HandlerList.unregisterAll(this);

        Bolster.getCooldownManager().clearCooldown(this.getCaster(), this);
    }
}


