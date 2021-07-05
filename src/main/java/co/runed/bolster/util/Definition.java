package co.runed.bolster.util;

import co.runed.bolster.util.registries.DefinitionRegistry;
import co.runed.bolster.util.registries.IRegisterable;
import co.runed.bolster.util.registries.Registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

public abstract class Definition<T extends IRegisterable> implements IRegisterable, ICategorised, INameable
{
    String name;
    String id;
    Callable<? extends T> callable;
    List<Category> categories = new ArrayList<>();

    public Definition(String id, Callable<T> callable)
    {
        this.id = id;
        this.name = id;
        this.callable = callable;
    }

    public Definition<T> setName(String name)
    {
        this.name = name;

        return this;
    }

    public Definition<T> category(Category category)
    {
        this.addCategory(category);

        return this;
    }

    public Definition<T> register()
    {
        if (getRegistry().contains(this.id)) return this;

        DefinitionRegistry<T> registry = getRegistry();
        Registry<T> internalRegistry = registry.getInternalRegistry();

        internalRegistry.addCategories(id, categories);
        registry.register(id, this);

        registry.getEntry(id).addCategories(internalRegistry.getEntry(id).getCategories());

        return this;
    }

    public T create()
    {
        return getRegistry().getInternalRegistry().get(this.id);
    }

    public Callable<? extends T> getCallable()
    {
        return callable;
    }

    public abstract DefinitionRegistry<T> getRegistry();

    @Override
    public String getId()
    {
        return this.id;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void addCategory(Category category)
    {
        this.categories.add(category);
    }

    @Override
    public Collection<Category> getCategories()
    {
        return this.categories;
    }
}
