package co.runed.bolster.abilities;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.conditions.AbilityOffCooldownCondition;
import co.runed.bolster.abilities.conditions.Condition;
import co.runed.bolster.abilities.conditions.ConditionPriority;
import co.runed.bolster.abilities.conditions.HasManaCondition;
import co.runed.bolster.properties.Properties;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.*;

public abstract class Ability implements Listener
{
    private final String id = UUID.randomUUID().toString();
    private String description;
    private double cooldown = 0;
    private float manaCost = 0;
    private Boolean cancelEventOnCast = false;
    private LivingEntity caster;
    private AbilityProvider abilitySource;

    private final List<ConditionData> conditions = new ArrayList<>();

    public Ability()
    {
        Bolster.getInstance().getServer().getPluginManager().registerEvents(this, Bolster.getInstance());

        this.addCondition(new AbilityOffCooldownCondition(), ConditionPriority.LOWEST);
        this.addCondition(new HasManaCondition(), ConditionPriority.LOWEST);
    }

    public String getId()
    {
        return this.getAbilitySource().getId() + "." + this.id;
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

    public AbilityProvider getAbilitySource()
    {
        return this.abilitySource;
    }

    public void setAbilitySource(AbilityProvider abilitySource)
    {
        this.abilitySource = abilitySource;
    }

    public void addCondition(Condition condition)
    {
        this.addCondition(condition, true);
    }

    public void addCondition(Condition condition, boolean result)
    {
        this.addCondition(condition, result, ConditionPriority.NORMAL);
    }

    public void addCondition(Condition condition, ConditionPriority priority)
    {
        this.addCondition(condition, true, priority);
    }

    public void addCondition(Condition condition, boolean result, ConditionPriority priority)
    {
        this.conditions.add(new ConditionData(condition, result, priority));
    }

    public double getCooldown()
    {
        return this.cooldown;
    }

    public void setCooldown(double cooldownSeconds)
    {
        this.cooldown = cooldownSeconds;
    }

    public double getRemainingCooldown()
    {
        return Bolster.getCooldownManager().getRemainingTime(this.getCaster(), this.id);
    }

    public boolean isOnCooldown()
    {
        return this.getRemainingCooldown() > 0;
    }

    public void clearCooldown()
    {
        Bolster.getCooldownManager().clearCooldown(this.getCaster(), this.id);
    }

    public void setShouldCancelEvent(Boolean cancelEventOnCast)
    {
        this.cancelEventOnCast = cancelEventOnCast;
    }

    public Boolean shouldCancelEvent()
    {
        return cancelEventOnCast;
    }

    public boolean canActivate(Properties properties)
    {
        if (!properties.contains(AbilityProperties.CASTER)) return false;

        Collections.sort(this.conditions);

        for (ConditionData data : this.conditions)
        {
            Condition condition = data.condition;

            boolean result = condition.evaluate(this, properties);

            if (result != data.result)
            {
                condition.onFail(this, properties);

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

            Bolster.getCooldownManager().setCooldown(this.getCaster(), this.id, this.getCooldown());

            if (properties.get(AbilityProperties.EVENT) != null)
            {
                Event event = properties.get(AbilityProperties.EVENT);

                if (event instanceof Cancellable && this.shouldCancelEvent())
                {
                    ((Cancellable) event).setCancelled(true);
                }
            }

            this.onPostActivate(properties);

            Bolster.getManaManager().addCurrentMana(this.getCaster(), -this.getManaCost());

            return true;
        }

        return false;
    }

    public abstract void onActivate(Properties properties);

    public void onPostActivate(Properties properties)
    {

    }

    public void destroy()
    {
        HandlerList.unregisterAll(this);
    }

    private static class ConditionData implements Comparable<ConditionData>
    {
        Condition condition;
        boolean result;
        public ConditionPriority priority;

        public ConditionData(Condition condition, boolean result, ConditionPriority priority)
        {
            this.condition = condition;
            this.result = result;
            this.priority = priority;
        }

        @Override
        public int compareTo(ConditionData condition)
        {
            return this.priority.compareTo(condition.priority);
        }
    }
}


