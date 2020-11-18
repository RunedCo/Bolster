package co.runed.bolster.abilities;

import co.runed.bolster.classes.BolsterClass;
import co.runed.bolster.managers.AbilityManager;
import co.runed.bolster.util.StringUtil;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.registries.IRegisterable;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Class that handles common functionality for prociding abilities
 * Used by {@link BolsterClass} and {@link co.runed.bolster.items.Item}
 */
public abstract class AbilityProvider implements IRegisterable
{
    private final List<AbilityData> abilities = new ArrayList<>();
    private LivingEntity entity;
    LivingEntity parent;

    @Override
    public abstract String getId();

    public abstract void onCastAbility(Ability ability, Boolean success);

    public abstract void onToggleCooldown(Ability ability);

    @Override
    public void create(ConfigurationSection config)
    {
        
    }

    public LivingEntity getEntity()
    {
        return this.entity;
    }

    public void setEntity(LivingEntity entity)
    {
        this.entity = entity;

        for (AbilityData abilityData : this.abilities)
        {
            Ability ability = abilityData.ability;

            if (ability.getCaster() == entity) continue;

            ability.setCaster(entity);
            AbilityManager.getInstance().add(entity, abilityData.trigger, ability);
        }
    }

    public LivingEntity getParent()
    {
        return parent;
    }

    public void setParent(LivingEntity parent)
    {
        this.parent = parent;
    }

    public void addAbility(AbilityTrigger trigger, BiConsumer<LivingEntity, Properties> lambda)
    {
        this.addAbility(trigger, new LambdaAbility(lambda));
    }

    public void forwardAbilityTrigger(AbilityTrigger from, AbilityTrigger to)
    {
        this.addAbility(from, new ForwardTriggerAbility(to));
    }

    public void addAbility(AbilityTrigger trigger, Ability ability)
    {
        ability.setAbilityProvider(this);

        AbilityData data = new AbilityData(trigger, ability);

        this.abilities.add(data);

        ability.setId(this.abilities.size() + "");
    }

    public boolean hasAbility(AbilityTrigger trigger)
    {
        return this.abilities.stream().anyMatch((info) -> info.trigger.equals(trigger));
    }

    @Override
    public String getDescription()
    {
        List<String> abilityDescriptions = new ArrayList<>();

        for (AbilityData abilityData : this.getAbilities())
        {
            Ability ability = abilityData.ability;

            if (ability.getDescription() == null) continue;

            String abilityName = ChatColor.RED + abilityData.trigger.getDisplayName() + (ability.getName() != null ? " - " + ability.getName() : "");

            String abilityDesc = abilityName + ": " + ChatColor.YELLOW + ability.getDescription();

            if (ability.getCooldown() > 0)
                abilityDesc += ChatColor.DARK_GRAY + " (" + abilityData.ability.getCooldown() + "s cooldown)";
            if (ability.getManaCost() > 0)
                abilityDesc += ChatColor.BLUE + " (" + abilityData.ability.getManaCost() + " mana)";

            abilityDescriptions.add(abilityDesc + ChatColor.RESET);
        }

        if (abilityDescriptions.size() <= 0) return null;

        return StringUtil.join("\n", abilityDescriptions);
    }

    public List<AbilityData> getAbilities()
    {
        return this.abilities;
    }

    public void destroy()
    {
        if (this.getEntity() != null)
        {
            AbilityManager.getInstance().trigger(this.getEntity(), this, AbilityTrigger.REMOVE, new Properties());
        }

        for (AbilityData abilityData : this.abilities)
        {
            AbilityManager.getInstance().remove(this.getEntity(), abilityData.ability);
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