package co.runed.bolster.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;

public interface ICategorised
{
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
