package co.runed.bolster.registries;

import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class Registry<T>
{
    public Plugin plugin;
    private final HashMap<String, Callable<? extends T>> entries = new HashMap<>();

    public Registry(Plugin plugin)
    {
        this.plugin = plugin;
    }

    public void register(String id, Class<? extends T> itemClass)
    {
        this.entries.putIfAbsent(id, () -> this.createFromClass(itemClass));
    }

    public void register(String id, Callable<? extends T> func)
    {
        this.entries.putIfAbsent(id, func);
    }

    public boolean contains(String id)
    {
        return this.entries.containsKey(id);
    }

    public Map<String, Callable<? extends T>> getEntries()
    {
        return this.entries;
    }

    public String getId(Class<? extends T> iClass)
    {
        for (Map.Entry<String, Callable<? extends T>> entry : this.entries.entrySet())
        {
            T instance = this.createInstance(entry.getKey());

            if (instance.getClass() == iClass)
            {
                return entry.getKey();
            }
        }

        return null;
    }

    private T createFromClass(Class<? extends T> iClass)
    {
        try
        {
            Constructor<? extends T> constructor = iClass.getConstructor();
            return constructor.newInstance();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public T createInstance(Class<? extends T> iClass)
    {
        for (Map.Entry<String, Callable<? extends T>> entry : this.entries.entrySet())
        {
            T instance = this.createInstance(entry.getKey());

            if (instance.getClass() == iClass)
            {
                return instance;
            }
        }

        return null;
    }

    public T createInstance(String id)
    {
        if (!this.entries.containsKey(id)) return null;

        try
        {
            return this.entries.get(id).call();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
