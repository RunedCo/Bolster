package co.runed.bolster.util.registries;

import co.runed.bolster.util.Category;

import java.util.ArrayList;
import java.util.Collection;

public interface IRegisterable
{
    void setId(String id);

    String getId();

    default void addCategory(Category category)
    {
    }

    default Collection<Category> getCategories()
    {
        return new ArrayList<>();
    }
}
