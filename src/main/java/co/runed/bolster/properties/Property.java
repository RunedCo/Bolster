package co.runed.bolster.properties;

import co.runed.bolster.Bolster;
import org.bukkit.NamespacedKey;

/**
 * A property passed to an ability when cast
 */
public class Property<T>
{
    private NamespacedKey key;
    private T defaultValue;

    public Property(String id)
    {
        this(new NamespacedKey(Bolster.getInstance(), id), null);
    }

    public Property(NamespacedKey id)
    {
        this(id, null);
    }

    public Property(String id, T defaultValue)
    {
        this(new NamespacedKey(Bolster.getInstance(), id), defaultValue);
    }

    public Property(NamespacedKey id, T defaultValue)
    {
        this.key = id;
        this.defaultValue = defaultValue;
    }

    public NamespacedKey getKey()
    {
        return this.key;
    }

    public String toString()
    {
        return "<Property " + this.key + ">";
    }

    public Property<T> setDefault(T defaultValue)
    {
        this.defaultValue = defaultValue;

        return this;
    }

    public T getDefault()
    {
        return this.defaultValue;
    }
}