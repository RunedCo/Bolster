package co.runed.bolster.game.traits;

import co.runed.bolster.common.gson.JsonExclude;
import co.runed.bolster.util.properties.Properties;

public abstract class TraitProvider
{
    private boolean enabled = false;

    @JsonExclude
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

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public boolean isEnabled()
    {
        return enabled;
    }
}
