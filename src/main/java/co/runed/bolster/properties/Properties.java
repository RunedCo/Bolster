package co.runed.bolster.properties;

import java.util.HashMap;
import java.util.Map;

public class Properties {
    private final Map<Property<?>, Object> values = new HashMap<>();

    public int size() {
        return this.values.size();
    }

    public boolean isEmpty() {
        return this.values.isEmpty();
    }

    public void clean() {
        this.values.clear();
    }

    public boolean contains(Property<?> key) {
        return this.values.containsKey(key);
    }

    public <T> T get(Property<T> key) {
        if(!this.values.containsKey(key)) return key.getDefault();

        return (T)this.values.get(key);
    }

    public <T> void set(Property<T> key, T value) {
        this.values.put(key, value);
    }
}
