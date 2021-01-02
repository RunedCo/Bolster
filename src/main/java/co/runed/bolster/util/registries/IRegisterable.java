package co.runed.bolster.util.registries;

import co.runed.bolster.util.Category;
import co.runed.bolster.util.ConfigUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;

// TODO SPLIT INTO MULTIPLE INTERFACES (IRegisterable, IConfigurable, ICategorised, and one for icon, display name, and description)
public interface IRegisterable
{
    String getId();

    String getDescription();

    // TODO RENAME?
    default void setConfig(ConfigurationSection config)
    {

    }

    default ConfigurationSection getConfig()
    {
        return ConfigUtil.create();
    }

    void create(ConfigurationSection config);

    default void addCategory(Category category)
    {
    }

    default Collection<Category> getCategories()
    {
        return new ArrayList<>();
    }

    default ItemStack getIcon()
    {
        return new ItemStack(Material.AIR);
    }
}
