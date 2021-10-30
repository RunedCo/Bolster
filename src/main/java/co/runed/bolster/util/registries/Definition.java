package co.runed.bolster.util.registries;

import co.runed.bolster.Bolster;
import co.runed.bolster.util.Categorised;
import co.runed.bolster.util.Category;
import co.runed.bolster.util.IconPreview;
import co.runed.bolster.util.ItemBuilder;
import co.runed.bolster.util.config.ConfigUtil;
import co.runed.bolster.util.config.Configurable;
import co.runed.dayroom.util.Identifiable;
import co.runed.dayroom.util.Nameable;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public abstract class Definition<T extends Identifiable> implements Identifiable, Nameable, Configurable, Categorised, IconPreview {

    private final String id;
    private String name;

    private final Function<Definition<T>, T> supplier;
    private final List<Category> categories = new ArrayList<>();
    private ItemStack icon;

    public Definition(String id, Function<Definition<T>, T> supplier) {
        this.id = id;
        this.supplier = supplier;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        if (name == null) return id;

        return name;
    }

    public Definition<T> from(Definition<T> parent) {
        for (var category : parent.categories) {
            addCategories(category);
        }

        return this;
    }

    public Definition<T> setName(String name) {
        this.name = name;

        return this;
    }

    @Override
    public ItemStack getIcon() {
        var builder = new ItemBuilder(icon)
                .setDisplayName(Component.text(getId()))
                .setDisplayName(Component.text(getName()));

        return builder.build();
    }

    public Definition<T> setIcon(ItemStack icon) {
        this.icon = icon;

        return this;
    }

    public Definition<T> addCategories(Category... categories) {
        for (var category : categories) {
            this.addCategory(category);
        }

        return this;
    }

    public Definition<T> register() {
        if (getRegistry() == null) {
            Bolster.getInstance().getLogger().severe("Error registering " + getId() + "! Registry is null!");
            return this;
        }

        getRegistry().register(getId(), this);

        return this;
    }

    public ConfigurationSection getConfig() {
        if (getRegistry() == null) {
//            Bolster.debug("Error fetching config for " + getId() + "! Registry is null!");
            return ConfigUtil.create();
        }

        return getRegistry().getConfig(getId());
    }

    public abstract @Nullable Registry<Definition<T>> getRegistry();

    protected T preCreate(T output) {
        if (output instanceof Configurable configurable) {
            configurable.loadConfig(getConfig());
        }

        return output;
    }


    public T create() {
        var result = this.supplier.apply(this);

        result = this.preCreate(result);

        return result;
    }

    @Override
    public void addCategory(Category category) {
        this.categories.add(category);
    }

    @Override
    public Collection<Category> getCategories() {
        return categories;
    }
}
