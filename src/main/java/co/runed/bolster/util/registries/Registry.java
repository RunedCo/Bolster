package co.runed.bolster.util.registries;

import co.runed.bolster.util.Category;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.Callable;

public class Registry<T extends IRegisterable>
{
    public Plugin plugin;
    private String folderName;
    private HashMap<String, ConfigurationSection> configs = new HashMap<>();
    private final HashMap<String, Entry<? extends T>> entries = new HashMap<>();

    public Registry(Plugin plugin)
    {
        this(plugin, null);
    }

    public Registry(Plugin plugin, String folderName)
    {
        this.plugin = plugin;
        this.folderName = folderName;

        if (folderName != null)
        {

        }
    }

    public void register(String id, Class<? extends T> itemClass)
    {
        this.register(id, () -> this.createFromClass(itemClass));
    }

    public void register(String id, T obj)
    {
        this.register(id, () -> obj);
    }

    public void register(T obj)
    {
        this.register(obj.getId(), () -> obj);
    }

    public void register(String id, Callable<? extends T> func)
    {
        this.entries.putIfAbsent(id, new Entry<>(id, func));
    }

    public boolean contains(String id)
    {
        return this.entries.containsKey(id);
    }

    public Map<String, Entry<? extends T>> getEntries()
    {
        return this.entries;
    }

    public List<Entry<? extends T>> getCategorised(Category category)
    {
        List<Entry<? extends T>> results = new ArrayList<>();

        for (Entry<? extends T> entry : this.entries.values())
        {
            if (entry.categories.contains(category)) results.add(entry);
        }

        return results;
    }

    public Class<T> getClass(String id)
    {
        return (Class<T>) this.createInstance(id).getClass();
    }

    public String getId(Class<? extends T> iClass)
    {
        for (Map.Entry<String, Entry<? extends T>> entry : this.entries.entrySet())
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
        for (Map.Entry<String, Entry<? extends T>> entry : this.entries.entrySet())
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
            T value = this.entries.get(id).create();

            value.setId(id);

            if (this.configs.containsKey(id))
            {

            }

            return value;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static class Entry<T extends IRegisterable>
    {
        String id;
        Collection<Category> categories;
        Callable<? extends T> function;

        public Entry(String id, Callable<? extends T> function)
        {
            this.id = id;
            this.function = function;

            T value = this.create();

            if (value != null) this.categories = value.getCategories();
        }

        public T create()
        {
            try
            {
                T value = this.function.call();

                value.setId(this.id);

                return value;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return null;
        }

        public Collection<? extends Category> getCategories()
        {
            return this.categories;
        }

        public String getId()
        {
            return id;
        }
    }
}
