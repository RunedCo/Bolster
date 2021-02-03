package co.runed.bolster.util.registries;

import co.runed.bolster.Bolster;
import co.runed.bolster.util.Category;
import co.runed.bolster.util.ConfigUtil;
import co.runed.bolster.util.ICategorised;
import co.runed.bolster.util.IConfigurable;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.Callable;

public class Registry<T extends IRegisterable>
{
    public Plugin plugin;
    private String folderName;
    private HashMap<String, ConfigurationSection> configs = new HashMap<>();
    private final HashMap<String, Entry<? extends T>> entries = new HashMap<>();

    private final HashMap<Class<? extends T>, String> classKeys = new HashMap<>();
    private final HashMap<T, String> objKeys = new HashMap<>();

    public Registry(Plugin plugin)
    {
        this(plugin, null);
    }

    public Registry(Plugin plugin, String folderName)
    {
        this.plugin = plugin;
        this.folderName = folderName;

        this.loadFiles(plugin, folderName);
    }

    public void loadFiles(Plugin plugin, String folderName)
    {
        if (folderName != null)
        {
            File pluginDir = plugin.getDataFolder();

            File specificFolder = new File(pluginDir, folderName);

            if (!specificFolder.exists() && !specificFolder.isDirectory()) return;

            for (File file : FileUtils.listFiles(specificFolder, new String[]{"yml", "yaml"}, true))
            {
                Configuration config = YamlConfiguration.loadConfiguration(file);

                for (String key : config.getKeys(false))
                {
                    if (!config.isConfigurationSection(key)) continue;

                    ConfigurationSection configSection = config.getConfigurationSection(key);

                    Bolster.getInstance().getLogger().info("Loaded config for " + key);

                    this.configs.put(key, configSection);
                }
            }
        }
    }

    public void register(String id, Class<? extends T> entryClass)
    {
        this.classKeys.put(entryClass, id);

        this.doRegister(id, () -> this.createFromClass(entryClass));
    }

    public void register(T obj)
    {
        if (obj.getId() == null) return;

        this.register(obj.getId(), obj);
    }

    public void register(String id, T obj)
    {
        this.objKeys.put(obj, id);

        this.doRegister(id, () -> obj);
    }

    public void register(String id, Callable<? extends T> func)
    {
        Class<? extends T> entryClass = null;

        try
        {
            entryClass = (Class<? extends T>) func.call().getClass();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        this.classKeys.put(entryClass, id);

        this.doRegister(id, func);
    }

    private void doRegister(String id, Callable<? extends T> func)
    {
        this.entries.putIfAbsent(id, new Entry<>(id, func, this.getConfig(id)));
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
        return (Class<T>) this.get(id).getClass();
    }

    public String getId(Class<? extends T> entryClass)
    {
        if (this.classKeys.containsKey(entryClass)) return this.classKeys.get(entryClass);

        return null;
    }

    public String getId(T obj)
    {
        if (this.objKeys.containsKey(obj)) return this.objKeys.get(obj);
        if (this.classKeys.containsKey(obj.getClass())) return this.classKeys.get(obj.getClass());

        return null;
    }

    public ConfigurationSection getConfig(String id)
    {
        ConfigurationSection config = ConfigUtil.create();

        if (this.configs.containsKey(id))
        {
            config = this.configs.get(id);
        }

        // HACKY CLONE
        config = ConfigUtil.cloneSection(config);

        return config;
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
            if (!(e instanceof NoSuchMethodException))
            {
                e.printStackTrace();
            }
        }

        return null;
    }

    public T get(Class<? extends T> entryClass)
    {
        if (!this.classKeys.containsKey(entryClass)) return null;

        return this.get(this.classKeys.get(entryClass));
    }

    public T get(String id)
    {
        if (!this.entries.containsKey(id)) return null;

        try
        {
            T value = this.entries.get(id).create();

            // TODO make sure id works without this set
            //value.setId(id);

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
        ConfigurationSection config;

        public Entry(String id, Callable<? extends T> function, ConfigurationSection config)
        {
            this.id = id;
            this.function = function;
            // HACKY CLONE
            this.config = config;

            T value = this.create();

            if (value != null) this.categories = value instanceof ICategorised ? ((ICategorised)value).getCategories() : new ArrayList<>();
        }

        public T create()
        {
            try
            {
                T value = this.function.call();

                // TODO make sure works without manually setting id
                //value.setId(this.id);

                ConfigurationSection config = ConfigUtil.create();

                if (this.config != null)
                {
                    config = this.config;
                }

                // HACKY CLONE
                config = ConfigUtil.cloneSection(config);

                if (value instanceof IConfigurable)
                {
                    IConfigurable configurable = (IConfigurable) value;

                    configurable.setConfig(config);
                    configurable.create(config);
                }

                return value;
            }
            catch (Exception e)
            {
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
