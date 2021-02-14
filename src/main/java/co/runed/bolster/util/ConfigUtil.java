package co.runed.bolster.util;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

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

    public static class BolsterConfiguration extends MemoryConfiguration
    {
        @Override
        public Object get(String path, Object def)
        {
            Object value = super.get(path, def);

            this.set(path, value);

            return value;
        }

        @Override
        public boolean isString(String path)
        {
            try
            {
                String test = (String) this.getString(path);
            }
            catch (Exception e)
            {
                return false;
            }

            return true;
        }

        @Override
        public boolean isInt(String path)
        {
            try
            {
                Integer test = (Integer) this.getInt(path);
            }
            catch (Exception e)
            {
                return false;
            }

            return true;
        }

        @Override
        public boolean isDouble(String path)
        {
            try
            {
                Double test = (Double) this.getDouble(path);
            }
            catch (Exception e)
            {
                return false;
            }

            return true;
        }

        @Override
        public boolean isLong(String path)
        {
            try
            {
                Long test = (Long) this.getLong(path);
            }
            catch (Exception e)
            {
                return false;
            }

            return true;
        }

        @Override
        public String getString(String path, String def)
        {
            if (this.isList(path))
            {
                return String.join(StringUtil.join(StringUtil.NEW_LINE, this.getStringList(path)));
            }

            return super.getString(path, def);
        }

        @Override
        public int getInt(String path, int def)
        {
            if (this.isList(path) && this.getList(path).size() >= 2)
            {
                List<Integer> range = this.getIntegerList(path);

                return ThreadLocalRandom.current().nextInt(range.get(0), range.get(1) + 1);
            }

            return super.getInt(path, def);
        }

        @Override
        public double getDouble(String path, double def)
        {
            if (this.isList(path) && this.getList(path).size() >= 2)
            {
                List<Double> range = this.getDoubleList(path);

                return ThreadLocalRandom.current().nextDouble(range.get(0), range.get(1) + 1);
            }

            return super.getDouble(path, def);
        }

        @Override
        public long getLong(String path, long def)
        {
            if (this.isList(path) && this.getList(path).size() >= 2)
            {
                List<Long> range = this.getLongList(path);

                return ThreadLocalRandom.current().nextLong(range.get(0), range.get(1) + 1);
            }

            return super.getLong(path, def);
        }
    }
}
