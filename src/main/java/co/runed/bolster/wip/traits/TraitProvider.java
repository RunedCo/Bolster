package co.runed.bolster.wip.traits;

import co.runed.bolster.util.properties.Properties;

public class TraitProvider
{
    private final Properties traits = new Properties();

    public Properties getTraits()
    {
        return traits;
    }

    public <T> void setTrait(Trait<T> key, T value)
    {
        this.traits.set(key, value);
    }

    public <T> T getTrait(Trait<T> key)
    {
        return this.traits.get(key);
    }
}
