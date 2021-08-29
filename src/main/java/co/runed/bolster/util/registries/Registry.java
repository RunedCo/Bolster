package co.runed.bolster.util.registries;

import co.runed.bolster.Bolster;
import co.runed.bolster.common.util.Identifiable;
import co.runed.bolster.events.server.ReloadConfigEvent;
import co.runed.bolster.util.Category;
import co.runed.bolster.util.ICategorised;
import co.runed.bolster.util.config.BolsterConfiguration;
import co.runed.bolster.util.config.ConfigUtil;
import co.runed.bolster.util.config.Configurable;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.*;
import java.util.concurrent.Callable;

public class Registry<T extends Identifiable> implements Listener {
    public Plugin plugin;
    private final List<File> configFolders = new ArrayList<>();
    private final Map<String, ConfigurationSection> configs = new HashMap<>();
    private final Map<String, Entry<? extends T>> entries = new HashMap<>();

    private final Map<Class<? extends T>, String> classKeys = new HashMap<>();
    private final Map<T, String> objKeys = new HashMap<>();
    private final Map<String, Collection<Category>> categories = new HashMap<>();

    public Registry(Plugin plugin) {
        this(plugin, null);
    }

    public Registry(Plugin plugin, String folderName) {
        this.plugin = plugin;
        this.loadFiles(plugin, folderName);

        Bukkit.getPluginManager().registerEvents(this, Bolster.getInstance());
    }

    public void loadFiles(Plugin plugin, String folderName) {
        if (folderName != null) {
            var pluginDir = plugin.getDataFolder();

            var folder = new File(pluginDir, folderName);

            if (!folder.exists() && !folder.isDirectory()) return;

            this.loadFiles(folder);
        }
    }

    public void loadFiles(File folder) {
        if (!this.configFolders.contains(folder)) this.configFolders.add(folder);

        for (var file : FileUtils.listFiles(folder, new String[]{"yml", "yaml"}, true)) {
            var config = BolsterConfiguration.loadConfiguration(file);

            for (var key : config.getKeys(false)) {
                if (!config.isConfigurationSection(key)) continue;

                var configSection = config.getConfigurationSection(key);

                Bolster.getInstance().getLogger().info("Loaded config for " + key);

                this.setConfig(key, configSection);
            }
        }
    }

    public void setConfig(String key, ConfigurationSection config) {
        this.configs.put(key, config);
    }

    public void reloadFiles() {
        this.configs.clear();

        for (var folder : this.configFolders) {
            this.loadFiles(folder);
        }
    }

    public void register(String id, Class<? extends T> entryClass) {
        this.classKeys.put(entryClass, id);

        this.doRegister(id, () -> this.createFromClass(entryClass));
    }

    public void register(T obj) {
        if (obj.getId() == null) return;

        this.register(obj.getId(), obj);
    }

    public void register(String id, T obj) {
        this.objKeys.put(obj, id);

        this.doRegister(id, () -> obj);
    }

    public void register(String id, Callable<? extends T> func) {
        Class<? extends T> entryClass = null;

        try {
            entryClass = (Class<? extends T>) func.call().getClass();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        this.classKeys.put(entryClass, id);

        this.doRegister(id, func);
    }

    protected void doRegister(String id, Callable<? extends T> func) {
        this.entries.putIfAbsent(id, new Entry<>(this, id, func, this.categories.getOrDefault(id, new ArrayList<>())));
    }

    public void addCategories(String id, Collection<Category> categories) {
        this.categories.putIfAbsent(id, new ArrayList<>());

        this.categories.get(id).addAll(categories);
    }

    public boolean contains(String id) {
        return this.entries.containsKey(id);
    }

    public Map<String, Entry<? extends T>> getEntries() {
        return this.entries;
    }

    public List<Entry<? extends T>> getCategorised(Category category) {
        List<Entry<? extends T>> results = new ArrayList<>();

        for (var entry : this.entries.values()) {
            if (entry.categories.contains(category)) results.add(entry);
        }

        return results;
    }

    public Class<T> getClass(String id) {
        return (Class<T>) this.get(id).getClass();
    }

    public String getId(Class<? extends T> entryClass) {
        if (this.classKeys.containsKey(entryClass)) return this.classKeys.get(entryClass);

        for (var entry : this.objKeys.entrySet()) {
            if (entry.getKey().getClass() == entryClass) return entry.getValue();
        }

        return null;
    }

    public String getId(T obj) {
        if (obj == null) return null;

        if (this.objKeys.containsKey(obj)) return this.objKeys.get(obj);
        if (this.classKeys.containsKey(obj.getClass())) return this.classKeys.get(obj.getClass());

        return null;
    }

    public ConfigurationSection getConfig(String id) {
        var config = ConfigUtil.create();

        if (this.configs.containsKey(id)) {
            config = this.configs.get(id);
        }

        // HACKY CLONE
        config = ConfigUtil.cloneSection(config);

        return config;
    }

    private T createFromClass(Class<? extends T> iClass) {
        try {
            var constructor = iClass.getConstructor();
            return constructor.newInstance();
        }
        catch (Exception e) {
            if (!(e instanceof NoSuchMethodException)) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public T get(Class<? extends T> entryClass) {
        if (!this.classKeys.containsKey(entryClass)) return null;

        return this.get(this.classKeys.get(entryClass));
    }

    public T get(String id) {
        if (!this.entries.containsKey(id)) return null;

        try {
            return this.entries.get(id).create();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Entry<? extends T> getEntry(String id) {
        if (!this.entries.containsKey(id)) return null;

        return this.entries.get(id);
    }

    @EventHandler
    private void onReload(ReloadConfigEvent event) {
        this.reloadFiles();
    }

    public static class Entry<T extends Identifiable> {
        private final Registry<T> parent;
        private String id;
        private Collection<Category> categories = new ArrayList<>();
        private Callable<? extends T> function;

        public Entry(Registry<T> parent, String id, Callable<? extends T> function, Collection<Category> categories) {
            this.parent = parent;
            this.id = id;
            this.function = function;

            this.categories.add(Category.ALL);
            addCategories(categories);

            try {
                var value = this.create();

                if (value instanceof ICategorised) {
                    addCategories(((ICategorised) value).getCategories());
                }
            }
            catch (Exception e) {
                System.out.println("Error getting categories...");
                e.printStackTrace();
            }
        }

        public T create() {
            try {
                var value = this.function.call();

                var config = parent.getConfig(id);

                // HACKY CLONE
                config = ConfigUtil.cloneSection(config);

                if (value instanceof Configurable) {
                    var configurable = (Configurable) value;

                    configurable.setConfig(config);
                    configurable.loadConfig(config);
                }

                return value;
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        public Collection<Category> getCategories() {
            return this.categories;
        }

        public void addCategories(Collection<Category> categories) {
            for (var category : categories) {
                if (this.categories.contains(category)) continue;

                this.categories.add(category);
            }
        }

        public String getId() {
            return id;
        }
    }
}
