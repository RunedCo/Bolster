package co.runed.bolster.util;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.util.Map;

public class ConfigUtil
{
    public static ConfigurationSection cloneSection(ConfigurationSection config)
    {
        return new MemoryConfiguration().createSection("clone", config.getValues(false));
    }

    public static ConfigurationSection fromMap(Map<String, Object> map)
    {
        return new MemoryConfiguration().createSection("map", map);
    }

    public static ConfigurationSection merge(ConfigurationSection config1, ConfigurationSection config2)
    {
        if (config1 == null || config2 == null) return config1;

        for (Map.Entry<String, Object> entry : config2.getValues(false).entrySet())
        {
            config1.set(entry.getKey(), entry.getValue());
        }

        return config1;
    }
}
