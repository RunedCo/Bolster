package co.runed.bolster.registries;

import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.util.HashMap;

public class Registry<T> {
    public Plugin plugin;
    private final HashMap<String, Class<? extends T>> entries = new HashMap<>();

    public Registry(Plugin plugin) {
        this.plugin = plugin;
    }

    public void register(String id, Class<? extends T> itemClass) {
        this.entries.putIfAbsent(id, itemClass);
    }

    public boolean contains(String id) {
        return this.entries.containsKey(id);
    }

    public T createInstance(String id) {
        if (!this.entries.containsKey(id)) return null;

        try {
            Class<? extends T> iClass = this.entries.get(id);
            Constructor<? extends T> constructor = iClass.getConstructor();

            return constructor.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
