package co.runed.bolster.classes;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityProvider;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.managers.AbilityManager;
import co.runed.bolster.managers.ClassManager;
import co.runed.bolster.managers.UpgradeManager;
import co.runed.bolster.util.Category;
import co.runed.bolster.util.ItemBuilder;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.registries.Registries;
import co.runed.bolster.wip.upgrade.Upgrade;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class BolsterClass extends AbilityProvider
{
    String name = null;
    ItemStack icon = new ItemStack(Material.PLAYER_HEAD);
    List<Category> categories = new ArrayList<>();
    List<Upgrade> upgrades = new ArrayList<>();
    String description;
    double maxHealth = 20;

    @Override
    public void create(ConfigurationSection config)
    {
        super.create(config);

        this.addAbility(AbilityTrigger.SET_CLASS, (entity, props) -> {
            if (this.maxHealth > 0)
            {
                entity.setMaxHealth(this.maxHealth);
                entity.setHealth(this.maxHealth);
            }
        });
    }

    public String getName()
    {
        if (name == null) return this.getId();

        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setMaxHealth(double maxHealth)
    {
        this.maxHealth = maxHealth;
    }

    public double getMaxHealth()
    {
        return maxHealth;
    }

    @Override
    public ItemStack getIcon()
    {
        return new ItemBuilder(this.icon).setDisplayName(this.getName()).setLore(this.getDescription()).build();
    }

    public void setIcon(ItemStack icon)
    {
        this.icon = icon;
    }

    @Override
    public String getId()
    {
        return Registries.CLASSES.getId(this);
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    @Override
    public String getDescription()
    {
        String out = super.getDescription();

        if (out != null && !out.isEmpty() && this.description != null && !this.description.isEmpty())
        {
            out += "\n\n" + ChatColor.WHITE + this.description;
        }

        return out;
    }

    @Override
    public void addCategory(Category category)
    {
        if (this.categories.contains(category)) return;

        this.categories.add(category);
    }

    @Override
    public Collection<Category> getCategories()
    {
        return this.categories;
    }

    @Override
    public void setEntity(LivingEntity entity)
    {
        boolean firstTime = this.getEntity() == null || !this.getEntity().getUniqueId().equals(entity.getUniqueId());

        if (entity.equals(this.getEntity())) return;

        super.setEntity(entity);

        if (firstTime)
        {
            AbilityManager.getInstance().trigger(entity, this, AbilityTrigger.SET_CLASS, new Properties());
        }

        ClassManager.getInstance().setClass(entity, this);
    }

    @Override
    public void onCastAbility(Ability ability, Boolean success)
    {

    }

    @Override
    public void onToggleCooldown(Ability ability)
    {

    }

    public void addUpgrade(Upgrade upgrade)
    {
        this.upgrades.add(upgrade);

        if (this.getEntity() != null)
        {
            UpgradeManager.getInstance().addUpgrade(this.getEntity(), upgrade);
        }
    }

    public void removeUpgrade(Upgrade upgrade)
    {
        this.upgrades.remove(upgrade);

        if (this.getEntity() != null)
        {
            UpgradeManager.getInstance().removeUpgrade(this.getEntity(), upgrade);
        }
    }

    @Override
    public boolean rebuild()
    {
        if (!this.isDirty()) return false;

        super.rebuild();

        if (this.getEntity() != null && !(this.getEntity() instanceof Player))
        {
            PersistentDataContainer data = this.getEntity().getPersistentDataContainer();
            data.set(ClassManager.CLASS_KEY, PersistentDataType.STRING, this.getId());
        }

        for (Upgrade upgrade : this.upgrades)
        {
            UpgradeManager.getInstance().addUpgrade(this.getEntity(), upgrade);
        }

        return true;
    }

    @Override
    public void destroy(boolean trigger)
    {
        super.destroy(trigger);

        if (this.getEntity() != null)
        {
            if (trigger)
                AbilityManager.getInstance().trigger(this.getEntity(), this, AbilityTrigger.REMOVE_CLASS, new Properties());
        }

        if (!(this.getEntity() instanceof Player))
        {
            this.getEntity().getPersistentDataContainer().remove(ClassManager.CLASS_KEY);
        }

        for (Upgrade upgrade : this.upgrades)
        {
            UpgradeManager.getInstance().removeUpgrade(this.getEntity(), upgrade);
        }

        // REMOVE UPGRADES
    }
}
