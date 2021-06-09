package co.runed.bolster.util.config;

import co.runed.bolster.util.ItemBuilder;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class ConfigUtil
{
    public static ConfigurationSection cloneSection(ConfigurationSection config)
    {
        if (config == null) return new BolsterConfiguration().createSection("clone");

        return new BolsterConfiguration().createSection("clone", config.getValues(false));
    }

    public static ConfigurationSection fromMap(Map<String, Object> map)
    {
        return new BolsterConfiguration().createSection("map", map);
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

    public static ConfigurationSection create()
    {
        return new BolsterConfiguration().createSection("config");
    }

    public static ItemStack parseItemStack(ConfigurationSection config)
    {
        if (config == null || !config.isString("type")) return new ItemStack(Material.AIR);

        Material material = Material.matchMaterial(config.getString("type", "air"));

        ItemBuilder builder = new ItemBuilder(material);

        if (config.isString("name")) builder = builder.setDisplayName(config.getString("name"));
        if (config.isInt("custom-model-data")) builder = builder.setCustomModelData(config.getInt("custom-model-data"));
        if (config.isList("lore")) builder = builder.addLore(config.getStringList("lore"));

        return builder.build();
    }

    public static ConfigurationSection parseVariables(ConfigurationSection outConfig, ConfigurationSection... otherSources)
    {
        if (outConfig == null) outConfig = new BolsterConfiguration();
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

            value = LegacyComponentSerializer.legacyAmpersand().serialize(MiniMessage.get().parse(value));
            value = ChatColor.translateAlternateColorCodes('&', value);

            outConfig.set(key, value);
        }

        return outConfig;
    }

    public static YamlConfiguration toYaml(ConfigurationSection config)
    {
        YamlConfiguration out = new YamlConfiguration();

        merge(out, config);

        return out;
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

    // This is fancier than Map.putAll(Map)
    public static Map deepMerge(Map original, Map newMap)
    {
        for (Object key : newMap.keySet())
        {
            if (newMap.get(key) instanceof Map && original.get(key) instanceof Map)
            {
                Map originalChild = (Map) original.get(key);
                Map newChild = (Map) newMap.get(key);
                original.put(key, deepMerge(originalChild, newChild));
            }
            else if (newMap.get(key) instanceof List && original.get(key) instanceof List)
            {
                List originalChild = (List) original.get(key);
                List newChild = (List) newMap.get(key);
                for (Object each : newChild)
                {
                    if (!originalChild.contains(each))
                    {
                        originalChild.add(each);
                    }
                }
            }
            else
            {
                original.put(key, newMap.get(key));
            }
        }
        return original;
    }

}
