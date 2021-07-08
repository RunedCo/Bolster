package co.runed.bolster.util.registries;

import co.runed.bolster.util.Definition;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.Callable;

public class DefinitionRegistry<T extends IRegisterable> extends Registry<Definition<T>>
{
    private final Registry<T> internalRegistry;

    public DefinitionRegistry(Plugin plugin)
    {
        this(plugin, null);
    }

    public DefinitionRegistry(Plugin plugin, String folderName)
    {
        super(plugin, folderName);

        this.internalRegistry = new Registry<T>(plugin);
        loadFiles(plugin, folderName);
    }

//    @Override
//    public void loadFiles(Plugin plugin, String folderName)
//    {
//        if (internalRegistry != null) internalRegistry.loadFiles(plugin, folderName);
//    }

    @Override
    public void setConfig(String key, ConfigurationSection config)
    {
        if (internalRegistry != null) internalRegistry.setConfig(key, config);

        super.setConfig(key, config);
    }

    public String getIdFromValue(Class<? extends T> value)
    {
        return internalRegistry.getId(value);
    }

    public String getIdFromValue(T value)
    {
        return internalRegistry.getId(value);
    }

    @Override
    public Class<Definition<T>> getClass(String id)
    {
        return super.getClass(id);
    }

    @Override
    public String getId(Class<? extends Definition<T>> entryClass)
    {
        return super.getId(entryClass);
    }

    @Override
    public String getId(Definition<T> obj)
    {
        return super.getId(obj);
    }

    public T getValue(Class<? extends T> valueClass)
    {
        return internalRegistry.get(valueClass);
    }

    public T getValue(String id)
    {
        return internalRegistry.get(id);
    }

    @Override
    protected void doRegister(String id, Callable<? extends Definition<T>> func)
    {
        super.doRegister(id, func);

        internalRegistry.register(id, get(id).getCallable());
    }

    public Registry<T> getInternalRegistry()
    {
        return internalRegistry;
    }
}
