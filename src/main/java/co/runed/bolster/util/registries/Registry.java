package co.runed.bolster.util.registries;

import co.runed.bolster.Bolster;
import co.runed.bolster.util.Category;
import co.runed.bolster.util.ConfigUtil;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

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
        ConfigurationSection config = new MemoryConfiguration();
        if (this.configs.containsKey(id))
        {
            config = this.configs.get(id);
        }

        this.entries.putIfAbsent(id, new Entry<>(id, func, config));
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

            if (value != null) this.categories = value.getCategories();
        }

        public T create()
        {
            try
            {
                T value = this.function.call();

                value.setId(this.id);

                ConfigurationSection config = new MemoryConfiguration().createSection("config");

                if (this.config != null)
                {
                    config = this.config;
                }

                // HACKY CLONE
                config = ConfigUtil.cloneSection(config);

                value.setConfig(config);

                value.create(config);

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
