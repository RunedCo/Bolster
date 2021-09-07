package co.runed.bolster.util;

import java.util.ArrayList;
import java.util.Collection;

public interface Categorised {
    default void addCategory(Category category) {
    }

    default Collection<Category> getCategories() {
        return new ArrayList<>();
    }
}
