package co.runed.bolster.abilities;

import co.runed.bolster.Bolster;
import co.runed.bolster.classes.BolsterClass;
import co.runed.bolster.managers.AbilityManager;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.registries.IRegisterable;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiConsumer;

/**
 * Class that handles common functionality for prociding abilities
 * Used by {@link BolsterClass} and {@link co.runed.bolster.items.Item}
 */
public abstract class AbilityProvider implements IRegisterable
{
    private final Collection<AbilityData> abilities = new ArrayList<>();
    private LivingEntity owner;

    @Override
    public abstract String getId();

    public abstract void onCastAbility(Ability ability, Boolean success);

    public abstract void onToggleCooldown(Ability ability);

    public LivingEntity getOwner()
    {
        return this.owner;
    }

    public void setOwner(LivingEntity owner)
    {
        if (this.owner == owner) return;

        this.owner = owner;

        for (AbilityData abilityData : this.abilities)
        {
            Ability ability = abilityData.ability;

            if (ability.getCaster() == owner) continue;

            ability.setCaster(owner);
            AbilityManager.getInstance().add(owner, abilityData.trigger, ability);
        }
    }

    public void addAbility(AbilityTrigger trigger, BiConsumer<LivingEntity, Properties> lambda)
    {
        this.addAbility(trigger, new LambdaAbility(lambda));
    }

    public void addAbility(AbilityTrigger trigger, Ability ability)
    {
        ability.setAbilityProvider(this);

        AbilityData data = new AbilityData(trigger, ability);

        this.abilities.add(data);
    }

    public boolean hasAbility(AbilityTrigger trigger)
    {
        return this.abilities.stream().anyMatch((info) -> info.trigger.equals(trigger));
    }

    public Collection<AbilityData> getAbilities()
    {
        return this.abilities;
    }

    public void destroy()
    {
        if (this.getOwner() != null)
            AbilityManager.getInstance().trigger(this.getOwner(), this, AbilityTrigger.REMOVE, new Properties());

        for (AbilityData abilityData : this.abilities)
        {
            AbilityManager.getInstance().remove(this.getOwner(), abilityData.ability);
        }

        this.abilities.clear();
    }

    public static class AbilityData
    {
        public AbilityTrigger trigger;
        public Ability ability;

        public AbilityData(AbilityTrigger trigger, Ability ability)
        {
            this.trigger = trigger;
            this.ability = ability;
        }
    }
}