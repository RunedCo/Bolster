package co.runed.bolster.abilities;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.conditions.HoldingItemCondition;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbilityProvider {
    private final Collection<AbilityData> abilities = new ArrayList<>();
    private LivingEntity owner;

    public abstract String getId();

    public abstract void onCastAbility(Ability ability, Boolean success);

    public LivingEntity getOwner() {
        return this.owner;
    }

    public void setOwner(LivingEntity owner) {
        this.owner = owner;

        for (AbilityData abilityData : this.abilities) {
            Ability ability = abilityData.ability;

            if(ability.getCaster() == owner) continue;

            ability.setCaster(owner);
            Bolster.getAbilityManager().add(owner, abilityData.trigger, ability);
        }
    }

    public void addAbility(AbilityTrigger trigger, Ability ability) {
        ability.setAbilitySource(this);

        AbilityData data = new AbilityData(trigger, ability);

        this.abilities.add(data);
    }

    public boolean hasAbility(AbilityTrigger trigger) {
        return this.abilities.stream().anyMatch((info) -> info.trigger.equals(trigger));
    }

    public Collection<AbilityData> getAbilities() {
        return this.abilities;
    }

    public static class AbilityData {
        public AbilityTrigger trigger;
        public Ability ability;

        public AbilityData(AbilityTrigger trigger, Ability ability) {
            this.trigger = trigger;
            this.ability = ability;
        }
    }
}