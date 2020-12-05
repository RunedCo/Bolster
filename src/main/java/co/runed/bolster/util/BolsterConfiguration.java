package co.runed.bolster.util;

import org.bukkit.configuration.MemoryConfiguration;

public class BolsterConfiguration extends MemoryConfiguration
{
    @Override
    public Object get(String path, Object def)
    {
        Object value = super.get(path, def);

        this.set(path, value);

        return value;
    }
}
