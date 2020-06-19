package co.runed.bolster.abilities.properties;

import co.runed.bolster.Bolster;
import org.bukkit.NamespacedKey;

/**
 *  A property passed to an ability when cast
 */
public class AbilityProperty<T> {
    private NamespacedKey key;
    private T defaultValue;

    public AbilityProperty(String id) {
        this(new NamespacedKey(Bolster.getInstance(), id), null);
    }

    public AbilityProperty(NamespacedKey id) {
        this(id, null);
    }

    public AbilityProperty(NamespacedKey id, T defaultValue) {
        this.key = id;
        this.defaultValue = defaultValue;
    }

    public NamespacedKey getKey() {
        return this.key;
    }

    public String toString() {
        return "<AbilityProperty " + this.key + ">";
    }

    public AbilityProperty<T> setDefault(T defaultValue) {
        this.defaultValue = defaultValue;

        return this;
    }

    public T getDefault() {
        return this.defaultValue;
    }
}