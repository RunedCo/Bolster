package co.runed.bolster.abilities;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.base.FunctionAbility;
import co.runed.bolster.classes.BolsterClass;
import co.runed.bolster.entity.BolsterEntity;
import co.runed.bolster.game.Traits;
import co.runed.bolster.game.traits.TraitProvider;
import co.runed.bolster.managers.AbilityManager;
import co.runed.bolster.util.ICategorised;
import co.runed.bolster.util.IDescribable;
import co.runed.bolster.util.INameable;
import co.runed.bolster.util.StringUtil;
import co.runed.bolster.util.config.ConfigUtil;
import co.runed.bolster.util.config.IConfigurable;
import co.runed.bolster.util.json.JsonExclude;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.registries.IRegisterable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * Class that handles common functionality for prociding abilities
 * Used by {@link BolsterClass} and {@link co.runed.bolster.items.Item}
 */
public abstract class AbilityProvider extends TraitProvider implements IRegisterable, INameable, IDescribable, IConfigurable, ICategorised
{
    private final List<AbilityProvider.AbilityData> abilities = new ArrayList<>();
    private LivingEntity entity;
    private LivingEntity parent;
    private ConfigurationSection config;
    private String name = null;
    @JsonExclude
    private boolean dirty;
    @JsonExclude
    private String tooltip;

    public abstract AbilityProviderType getType();

    public abstract void onCastAbility(Ability ability, Boolean success);

    public abstract void onToggleCooldown(Ability ability);

    public void onEnable()
    {
        if (Bolster.getBolsterConfig().debugMode)
            Bolster.getInstance().getLogger().info(ChatColor.stripColor(this.getEntity().getName()) + " (" + this.getEntity().getUniqueId() + ")" + ": " + this.getId() + " enabled");

        double health = this.getTrait(Traits.MAX_HEALTH);

        this.getEntity().setMaxHealth(this.getEntity().getMaxHealth() + health);
        this.getEntity().setHealth(this.getEntity().getHealth() + health);
    }

    public void onDisable()
    {
        if (Bolster.getBolsterConfig().debugMode)
            Bolster.getInstance().getLogger().info(ChatColor.stripColor(this.getEntity().getName()) + " (" + this.getEntity().getUniqueId() + ")" + ": " + this.getId() + " disabled");

        double health = this.getEntity().getMaxHealth() - this.getTrait(Traits.MAX_HEALTH);

        if (health <= 0) health = 20;

        this.getEntity().setMaxHealth(health);
    }

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
    public boolean isConfigSet()
    {
        return this.config != null;
    }

    @Override
    public void create(ConfigurationSection config)
    {
        ConfigUtil.parseVariables(config);

        if (config != null) this.tooltip = config.getString("tooltip", null);
    }

    @Override
    public String getName()
    {
        if (name == null) return this.getId();

        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        if (this.isEnabled() != enabled && this.getEntity() != null)
        {
            if (!enabled)
            {
                this.onDisable();
                AbilityTrigger.DISABLE.trigger(this.getEntity(), this, new Properties());
            }

            if (enabled)
            {
                this.onEnable();
                AbilityTrigger.ENABLE.trigger(this.getEntity(), this, new Properties());
            }
        }

        super.setEnabled(enabled);
    }

    public LivingEntity getEntity()
    {
        return this.entity;
    }

    public void setEntity(LivingEntity entity)
    {
        this.setEntity(entity, true);
    }

    public void setEntity(LivingEntity entity, boolean trigger)
    {
        boolean firstTime = this.getEntity() == null || (entity != null && !this.getEntity().getUniqueId().equals(entity.getUniqueId()));

        if (entity != null && entity.equals(this.getEntity())) return;

        this.entity = entity;

        this.setDirty();

        if (firstTime && trigger)
        {
            this.rebuild();

            // FIXME move to better place
            this.onEnable();

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

        this.setDirty();
    }

    public void addAbility(AbilityTrigger trigger, BiConsumer<LivingEntity, Properties> lambda)
    {
        this.addAbility(trigger, new FunctionAbility(lambda));
    }

    public void addAbility(AbilityTrigger trigger, Ability ability)
    {
        ability.setAbilityProvider(this);
        ability.setTrigger(trigger);
        if (this.getEntity() != null) ability.setCaster(this.getEntity());

        if (ability.getId() == null)
        {
            Bolster.getInstance().getLogger().severe("Ability " + ability.getName() + "(" + ability.getClass().toString() + ") from " + this.getId() + " is missing an ID...");
        }

        // TODO potentially only create the AbilityData at a later point?
        AbilityProvider.AbilityData data = new AbilityProvider.AbilityData(trigger, ability);
        this.abilities.add(data);

        // TODO set id dynamically?
        //ability.setId(this.abilities.size() + "");
    }

    public void removeAbility(Ability ability)
    {
        Optional<AbilityData> filtered = abilities.stream().filter((data) -> data.ability == ability).findFirst();

        if (!filtered.isPresent()) return;

        AbilityProvider.AbilityData data = filtered.get();

        abilities.remove(data);

        data.destroy();
    }

    public boolean hasAbility(AbilityTrigger trigger)
    {
        return this.abilities.stream().anyMatch((info) -> info.trigger.equals(trigger));
    }

    public List<AbilityProvider.AbilityData> getAbilities()
    {
        return this.abilities;
    }

    public boolean isDirty()
    {
        return this.dirty;
    }

    public void setDirty()
    {
        this.dirty = true;
    }

    @Override
    public String getDescription()
    {
        List<String> abilityDescriptions = new ArrayList<>();

        for (AbilityProvider.AbilityData abilityData : this.getAbilities())
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
            if (ability.getCharges() > 1)
                abilityDesc += ChatColor.BLUE + " (" + ability.getCharges() + " charges";
            if (ability.getCastTime() > 0)
                abilityDesc += ChatColor.BLUE + " (" + ability.getCastTime() + "s cast time)";

            abilityDescriptions.add(abilityDesc + ChatColor.RESET);
        }

        if (abilityDescriptions.size() <= 0) return null;

        return StringUtil.join(StringUtil.NEW_LINE, abilityDescriptions);
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

            for (AbilityProvider.AbilityData abilityData : this.abilities)
            {
                Ability ability = abilityData.ability;

                if (ability.getCaster() == entity) continue;

                ability.setCaster(entity);
                // TODO
                // AbilityManager.getInstance().add(entity, abilityData.trigger, ability);
            }
        }

        return true;
    }

    public void destroy()
    {
        this.destroy(true);
    }

    // FIXME this whole destroying every time the entity is set is a pain in the ass and needs work
    public void destroy(boolean trigger)
    {
        if (this.getEntity() != null)
        {
            if (trigger)
            {
                this.onDisable();

                AbilityManager.getInstance().trigger(this.getEntity(), this, AbilityTrigger.DESTROY, new Properties());
            }

            BolsterEntity.from(this.getEntity()).removeTraitProvider(this);
        }

        List<AbilityData> abilityList = new ArrayList<>(this.abilities);
        for (AbilityProvider.AbilityData abilityData : abilityList)
        {
            abilityData.destroy();
        }

        this.abilities.clear();
    }

    public static class AbilityData
    {
        public AbilityTrigger trigger;
        public Ability ability;
        public BukkitTask task = null;

        public AbilityData(AbilityTrigger trigger, Ability ability)
        {
            this.trigger = trigger;
            this.ability = ability;

            if (this.trigger == AbilityTrigger.TICK)
            {
                this.task = Bukkit.getServer().getScheduler().runTaskTimer(Bolster.getInstance(), this::run, 0L, 1L);
            }
        }

        protected void run()
        {
            if (ability.getCaster() == null) return;
            if (ability.isOnCooldown()) return;

            Properties properties = new Properties();

            AbilityManager.getInstance().trigger(ability.getCaster(), this.trigger, properties);
        }

        public void destroy()
        {
            ability.destroy();

            if (this.task != null)
            {
                this.task.cancel();
            }
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj instanceof AbilityProvider.AbilityData)
            {
                AbilityProvider.AbilityData data = (AbilityProvider.AbilityData) obj;

                return data.ability.equals(this.ability) && data.trigger.equals(this.trigger);
            }

            return super.equals(obj);
        }
    }
}