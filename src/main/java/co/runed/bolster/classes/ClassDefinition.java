package co.runed.bolster.classes;

import co.runed.bolster.util.Category;
import co.runed.bolster.util.Definition;
import co.runed.bolster.util.registries.DefinitionRegistry;
import co.runed.bolster.util.registries.Registries;

import java.util.concurrent.Callable;

public class ClassDefinition extends Definition<BolsterClass>
{
    public ClassDefinition(String id, Callable<BolsterClass> callable)
    {
        super(id, callable);
    }

    @Override
    public ClassDefinition category(Category category)
    {
        return (ClassDefinition) super.category(category);
    }

    @Override
    public ClassDefinition register()
    {
        return (ClassDefinition) super.register();
    }

    @Override
    public DefinitionRegistry<BolsterClass> getRegistry()
    {
        return Registries.CLASSES;
    }
}
