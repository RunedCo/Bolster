package co.runed.bolster.util;

import org.apache.commons.lang.StringUtils;
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

    public static ConfigurationSection parseVariables(ConfigurationSection outConfig, ConfigurationSection... otherSources)
    {
        ConfigurationSection sourceConfig = ConfigUtil.cloneSection(outConfig);

        for (ConfigurationSection source : otherSources)
        {
            ConfigUtil.merge(sourceConfig, source);
        }

        for (String key : outConfig.getKeys(true))
        {
            if (!outConfig.isString(key)) continue;

            String value = outConfig.getString(key);

            if (value == null) continue;

            value = iterateVariables(value, sourceConfig);

            outConfig.set(key, value);
        }

        return outConfig;
    }

    private static String iterateVariables(String value, ConfigurationSection config)
    {
        String[] matches = StringUtils.substringsBetween(value, "%", "%");

        if (matches != null && matches.length > 0)
        {
            for (String match : matches)
            {
                if (config.isSet(match))
                {
                    String foundValue = String.valueOf(config.get(match));

                    value = iterateVariables(value.replaceAll("%" + match + "%", foundValue), config);
                }
            }
        }

        return value;
    }
}