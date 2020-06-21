package co.runed.bolster.registries;

import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

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

    public Map<String, Class<? extends T>> getEntries() {
        return this.entries;
    }

    public String getId(Class<? extends T> iClass) {
        for (Map.Entry<String, Class<? extends T>> entry : this.entries.entrySet()) {
            if(entry.getValue() == iClass) {
                return entry.getKey();
            }
        }

        return null;
    }

    public T createInstance(Class<? extends T> iClass) {
        if (!this.entries.containsValue(iClass)) return null;

        try {
            Constructor<? extends T> constructor = iClass.getConstructor();

            return constructor.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public T createInstance(String id) {
        if (!this.entries.containsKey(id)) return null;

        return this.createInstance(this.entries.get(id));
    }
}
