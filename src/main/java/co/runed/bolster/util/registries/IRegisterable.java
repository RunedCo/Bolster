package co.runed.bolster.util.registries;

import co.runed.bolster.util.Category;
import co.runed.bolster.util.ConfigUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;

public interface IRegisterable
{
    void setId(String id);

    String getId();

    String getDescription();

    // TODO RENAME?
    default void setConfig(ConfigurationSection config)
    {

    }

    default ConfigurationSection getConfig()
    {
        return new MemoryConfiguration();
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
