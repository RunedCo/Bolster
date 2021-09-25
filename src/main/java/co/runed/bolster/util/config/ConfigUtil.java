package co.runed.bolster.util.config;

import co.runed.bolster.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ConfigUtil {
    public static ConfigurationSection cloneSection(ConfigurationSection config) {
        if (config == null) return new BolsterConfiguration().createSection("clone");

        return new BolsterConfiguration().createSection("clone", config.getValues(false));
    }

    public static ConfigurationSection fromMap(Map<String, Object> map) {
        return new BolsterConfiguration().createSection("map", map);
    }

    public static ConfigurationSection merge(ConfigurationSection config1, ConfigurationSection config2) {
        if (config1 == null || config2 == null) return config1;

        for (var entry : config2.getValues(false).entrySet()) {
            config1.set(entry.getKey(), entry.getValue());
        }

        return config1;
    }

    public static ConfigurationSection create() {
        return new BolsterConfiguration().createSection("config");
    }

    public static ItemStack parseItemStack(ConfigurationSection config) {
        if (config == null || !config.isString("type")) return new ItemStack(Material.AIR);

        var material = Material.matchMaterial(config.getString("type", "air"));

        var builder = new ItemBuilder(material);

        if (config.isString("name")) builder = builder.setDisplayName(Component.text(config.getString("name")));
        if (config.isInt("custom-model-data")) builder = builder.setCustomModelData(config.getInt("custom-model-data"));
        if (config.isList("lore")) builder = builder.addLore(config.getStringList("lore"));

        return builder.build();
    }

    public static ConfigurationSection parseVariables(ConfigurationSection outConfig, ConfigurationSection... otherSources) {
        if (outConfig == null) outConfig = new BolsterConfiguration();
        var sourceConfig = ConfigUtil.cloneSection(outConfig);

        // TODO: fix badly named variable sources e.g (.runeblade.milestone vs just milestone)

        for (var source : otherSources) {
            ConfigUtil.merge(sourceConfig, source);
        }

        for (var key : outConfig.getKeys(true)) {
            if (!outConfig.isString(key)) continue;

            var value = outConfig.getString(key);

            if (value == null) continue;

            value = iterateVariables("%", value, toStringMap(sourceConfig, true), false);

            value = LegacyComponentSerializer.legacyAmpersand().serialize(MiniMessage.get().parse(value));
            value = ChatColor.translateAlternateColorCodes('&', value);

            outConfig.set(key, value);
        }

        return outConfig;
    }

    public static YamlConfiguration toYaml(ConfigurationSection config) {
        var out = new YamlConfiguration();

        merge(out, config);

        return out;
    }

    public static Map<String, String> toStringMap(ConfigurationSection config, boolean deep) {
        Map<String, String> strMap = new HashMap<>();

        for (var entry : config.getValues(deep).entrySet()) {
            var value = entry.getValue();

            if (value instanceof ConfigurationSection) continue;

            if (entry.getValue() instanceof ArrayList list) {
                var out = (List<String>) list.stream().map(e -> e.toString()).collect(Collectors.toList());

                value = String.join("\n", out);
            }

            strMap.put(entry.getKey(), value.toString());
        }

        return strMap;
    }

    // Iterates through string to replace variables from map. Doesn't include text inside of pre tags.
    public static String iterateVariables(String token, String value, Map<String, String> variables, boolean parseInsidePre) {
        var preRegex = Pattern.compile("<pre\\b[^>]*>(.*?)</pre>");
        var matcher = preRegex.matcher(value);

        var sections = new LinkedHashMap<String, Boolean>();
        var lastIndex = 0;

        // TODO: parse before and after pre sections
        while (matcher.find()) {
            var start = matcher.start();
            var end = matcher.end();

            sections.put(value.substring(lastIndex, start), true);

            var group = matcher.group();
            sections.put(group, parseInsidePre);

            lastIndex = end;
        }

        if (lastIndex < value.length()) sections.put(value.substring(lastIndex), true);

        var parsedSections = new ArrayList<String>();

        for (var entry : sections.entrySet()) {
            var section = entry.getKey();
            var parse = entry.getValue();

            if (parse) {
                var matchArray = StringUtils.substringsBetween(section, token, token);
                var matches = Arrays.asList(matchArray == null ? new String[0] : matchArray);
                var skip = Collections.disjoint(matches, variables.keySet()) || matches.size() <= 0;

                for (var match : matches) {
                    if (variables.containsKey(match)) {
                        var foundValue = String.valueOf(variables.get(match));

                        section = section.replaceAll(token + match + token, foundValue);
                    }
                }

                if (!skip) {
                    section = iterateVariables(token, section, variables, parseInsidePre);
                }
            }

            parsedSections.add(section);
        }

        return String.join("", parsedSections);
    }

    // This is fancier than Map.putAll(Map)
    public static Map deepMerge(Map original, Map newMap) {
        for (var key : newMap.keySet()) {
            if (newMap.get(key) instanceof Map && original.get(key) instanceof Map) {
                var originalChild = (Map) original.get(key);
                var newChild = (Map) newMap.get(key);
                original.put(key, deepMerge(originalChild, newChild));
            }
            else if (newMap.get(key) instanceof List && original.get(key) instanceof List) {
                var originalChild = (List) original.get(key);
                var newChild = (List) newMap.get(key);
                for (var each : newChild) {
                    if (!originalChild.contains(each)) {
                        originalChild.add(each);
                    }
                }
            }
            else {
                original.put(key, newMap.get(key));
            }
        }
        return original;
    }

    // Mimics Bukkit's serialization. Includes the type key of the given ConfigurationSerializable.
    public static Map<String, Object> serialize(ConfigurationSerializable serializable) {
        if (serializable == null) return null;
        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, ConfigurationSerialization.getAlias(serializable.getClass()));
        dataMap.putAll(serializable.serialize());
        return dataMap;
    }

    // Expects the Map to contain a type key.
    public static <T extends ConfigurationSerializable> T deserialize(Map<String, Object> dataMap) {
        if (dataMap == null) return null;
        try {
            return (T) ConfigurationSerialization.deserializeObject(dataMap);
        }
        catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Could not deserialize object", ex);
        }
    }

    public static boolean areEqual(ConfigurationSection config, ConfigurationSection config1) {
        if (config == null || config1 == null) return false;

        return config.getValues(true).equals(config1.getValues(true));
    }
}
