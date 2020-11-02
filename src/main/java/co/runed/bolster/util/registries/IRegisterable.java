package co.runed.bolster.util.registries;

import co.runed.bolster.util.Category;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;

public interface IRegisterable
{
    void setId(String id);

    String getId();

    String getDescription();

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
