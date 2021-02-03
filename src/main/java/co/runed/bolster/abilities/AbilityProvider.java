package co.runed.bolster.abilities;

import co.runed.bolster.BolsterEntity;
import co.runed.bolster.classes.BolsterClass;
import co.runed.bolster.managers.AbilityManager;
import co.runed.bolster.util.ConfigUtil;
import co.runed.bolster.util.ICategorised;
import co.runed.bolster.util.IConfigurable;
import co.runed.bolster.util.StringUtil;
import co.runed.bolster.util.json.JsonExclude;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.registries.IRegisterable;
import co.runed.bolster.wip.traits.TraitProvider;
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
public abstract class AbilityProvider extends TraitProvider implements IRegisterable, IConfigurable, ICategorised
{
    private final List<AbilityData> abilities = new ArrayList<>();
    private LivingEntity entity;
    LivingEntity parent;
    private ConfigurationSection config;
    private boolean enabled = true;
    private boolean persistent;
    @JsonExclude
    private boolean dirty;

    public abstract AbilityProviderType getType();

    public abstract void onCastAbility(Ability ability, Boolean success);

    public abstract void onToggleCooldown(Ability ability);

    @Override
    public void setConfig(ConfigurationSection config)
    {
        this.config = ConfigUtil.cloneSection(config);
    }

    @Override
    public ConfigurationSection getConfig()
    {
        return this.config;
    }

    @Override
    public void create(ConfigurationSection config)
    {
        ConfigUtil.parseVariables(config);
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setPersistent(boolean persistent)
    {
        this.persistent = persistent;
    }

    public boolean isPersistent()
    {
        return persistent;
    }

    public LivingEntity getEntity()
    {
        return this.entity;
    }

    public void setEntity(LivingEntity entity)
    {
        boolean firstTime = this.getEntity() == null || !this.getEntity().getUniqueId().equals(entity.getUniqueId());

        if (entity.equals(this.getEntity())) return;

        this.entity = entity;

        this.markDirty();

        if (firstTime)
        {
            this.rebuild();

            AbilityManager.getInstance().trigger(this.getEntity(), this, AbilityTrigger.CREATE, new Properties());
        }
    }

    public LivingEntity getParent()
    {
        return parent;
    }

    public void setParent(LivingEntity parent)
    {
        this.parent = parent;

        this.markDirty();
    }

    public void addAbility(AbilityTrigger trigger, BiConsumer<LivingEntity, Properties> lambda)
    {
        this.addAbility(trigger, new LambdaAbility(lambda));
    }

    public void addAbility(AbilityTrigger trigger, Ability ability)
    {
        this.addAbility(trigger, ability, 0);
    }

    public void addAbility(AbilityTrigger trigger, Ability ability, int priority)
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

    public boolean isDirty()
    {
        return this.dirty;
    }

    public void markDirty()
    {
        this.dirty = true;
    }

    public boolean isConfigSet()
    {
        return this.config != null;
    }

    @Override
    public String getDescription()
    {
        List<String> abilityDescriptions = new ArrayList<>();

        for (AbilityData abilityData : this.getAbilities())
        {
            Ability ability = abilityData.ability;

            if (!ability.isEnabled()) continue;
            if (ability.getDescription() == null) continue;

            String abilityName = ChatColor.RED + abilityData.trigger.getDisplayName() + (ability.getName() != null ? " - " + ability.getName() : "");

            String abilityDesc = abilityName + ": " + ChatColor.YELLOW + ability.getDescription();

            if (ability.getCooldown() > 0)
                abilityDesc += ChatColor.DARK_GRAY + " (" + ability.getCooldown() + "s cooldown)";
            if (ability.getManaCost() > 0)
                abilityDesc += ChatColor.BLUE + " (" + ability.getManaCost() + " mana)";
            if (ability.getCastTime() > 0)
                abilityDesc += ChatColor.BLUE + " (" + ability.getCastTime() + "s cast time)";

            abilityDescriptions.add(abilityDesc + ChatColor.RESET);
        }

        if (abilityDescriptions.size() <= 0) return null;

        return StringUtil.join("\n", abilityDescriptions);
    }

    public List<AbilityData> getAbilities()
    {
        return this.abilities;
    }

    public boolean rebuild()
    {
        if (!this.isDirty()) return false;

        this.dirty = false;

        this.destroy(false);
        this.create(this.config);

        if (this.getEntity() != null)
        {
            BolsterEntity.from(entity).addTraitProvider(this);

            for (AbilityData abilityData : this.abilities)
            {
                Ability ability = abilityData.ability;

                if (ability.getCaster() == entity) continue;

                ability.setCaster(entity);
                AbilityManager.getInstance().add(entity, abilityData.trigger, ability);
            }
        }

        return true;
    }

    public void destroy()
    {
        this.destroy(true);
    }

    public void destroy(boolean trigger)
    {
        if (this.getEntity() != null)
        {
            if (trigger)
                AbilityManager.getInstance().trigger(this.getEntity(), this, AbilityTrigger.DESTROY, new Properties());

            BolsterEntity.from(this.getEntity()).removeTraitProvider(this);
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