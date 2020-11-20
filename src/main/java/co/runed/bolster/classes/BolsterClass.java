package co.runed.bolster.classes;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityProvider;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.managers.AbilityManager;
import co.runed.bolster.managers.ClassManager;
import co.runed.bolster.managers.UpgradeManager;
import co.runed.bolster.upgrade.Upgrade;
import co.runed.bolster.util.Category;
import co.runed.bolster.util.ItemBuilder;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
    String id;
    String name = null;
    ItemStack icon = new ItemStack(Material.PLAYER_HEAD);
    List<Category> categories = new ArrayList<>();
    List<Upgrade> upgrades = new ArrayList<>();
    String description;

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
    public ItemStack getIcon()
    {
        return new ItemBuilder(this.icon).setDisplayName(this.getName()).setLore(this.getDescription()).build();
    }

    public void setIcon(ItemStack icon)
    {
        this.icon = icon;
    }

    @Override
    public void setId(String id)
    {
        this.id = id;
    }

    @Override
    public String getId()
    {
        return this.id;
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
        super.setEntity(entity);

        ClassManager.getInstance().setClass(entity, this);

        if (!(entity instanceof Player))
        {
            PersistentDataContainer data = entity.getPersistentDataContainer();
            data.set(ClassManager.CLASS_KEY, PersistentDataType.STRING, this.getId());
        }

        for (Upgrade upgrade : this.upgrades)
        {
            UpgradeManager.getInstance().addUpgrade(this.getEntity(), upgrade);
        }

        // TODO: MOVE OUTSIDE OF CLASS SPECIFIC IMPLEMENTATION
        AbilityManager.getInstance().trigger(entity, this, AbilityTrigger.BECOME, new Properties());
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
    public void destroy()
    {
        super.destroy();

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
