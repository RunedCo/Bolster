package co.runed.bolster.wip;

import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.properties.Property;

public class TraitProvider
{
    private final Properties traits = new Properties();

    public Properties getTraits()
    {
        return traits;
    }

    public <T> void setTrait(Property<T> key, T value)
    {
        this.traits.set(key, value);
    }

    public <T> T getTrait(Property<T> key)
    {
        return this.traits.get(key);
    }
}
