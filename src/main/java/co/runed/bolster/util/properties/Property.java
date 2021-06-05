package co.runed.bolster.util.properties;

import co.runed.bolster.Bolster;
import co.runed.bolster.util.registries.IRegisterable;
import org.bukkit.NamespacedKey;

/**
 * A property passed to an ability when cast
 */
public class Property<T> implements IRegisterable
{
    private NamespacedKey key;
    private T defaultValue;

    public Property(String id)
    {
        this(id, null);
    }

    public Property(String id, T defaultValue)
    {
        this.key = new NamespacedKey(Bolster.getInstance(), id);
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

    public void setId(String id)
    {
        this.key = new NamespacedKey(Bolster.getInstance(), id);
    }

    @Override
    public String getId()
    {
        return this.getKey().toString();
    }

    @Override
    public String getDescription()
    {
        return null;
    }
}