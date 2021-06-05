package co.runed.bolster.util;

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
}
