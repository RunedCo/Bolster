package co.runed.bolster.items;

import co.runed.bolster.util.Category;
import co.runed.bolster.util.Definition;
import co.runed.bolster.util.registries.DefinitionRegistry;
import co.runed.bolster.util.registries.Registries;

import java.util.concurrent.Callable;

public class ItemDefinition extends Definition<Item>
{
    public ItemDefinition(String id, Callable<Item> callable)
    {
        super(id, callable);
    }

    @Override
    public ItemDefinition category(Category category)
    {
        return (ItemDefinition) super.category(category);
    }

    @Override
    public ItemDefinition register()
    {
        return (ItemDefinition) super.register();
    }

    @Override
    public DefinitionRegistry<Item> getRegistry()
    {
        return Registries.ITEMS;
    }
}
