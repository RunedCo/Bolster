package co.runed.bolster.util.lang;

import co.runed.bolster.Bolster;
import co.runed.bolster.util.config.ConfigUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Lang {
    private static final Map<String, String> langDictionary = new HashMap<>();

    private String defaultValue = "null";
    private Set<String> keys = new HashSet<>();
    private Map<String, String> replacements = new HashMap<>();
    private Set<LangProvider> providers = new HashSet<>();
    private List<Map<String, String>> languageSources = new ArrayList<>();

    public Lang(String... keys) {
        this.keys.addAll(Arrays.asList(keys));
    }

    public Lang with(LangProvider langProvider) {
        this.providers.add(langProvider);

        return this;
    }

    public Lang withDefault(String defaultValue) {
        this.defaultValue = defaultValue != null ? defaultValue : "invalid";

        return this;
    }

    public Lang withSource(Map<String, String> languageSource) {
        languageSources.add(languageSource);

        return this;
    }

    public Lang withReplacements(Map<String, String> keys) {
        replacements.putAll(keys);

        return this;
    }

    public Lang withReplacement(String key, String value) {
        var map = new HashMap<String, String>();
        map.put(key, value);

        return withReplacements(map);
    }

    public Lang withReplacement(String key, Component value) {
        return withReplacement(key, MiniMessage.get().serialize(value));
    }

    public Lang clone() {
        var cloned = new Lang(keys.toArray(new String[0]))
                .withReplacements(replacements)
                .withDefault(defaultValue);

        for (var provider : providers) {
            cloned = cloned.with(provider);
        }

        for (var source : languageSources) {
            cloned = cloned.withSource(source);
        }

        return cloned;
    }

    private String toFormattedString() {
        var key = defaultValue;
        var lang = new HashMap<>(langDictionary);
        var replacements = new HashMap<>(this.replacements);

        for (var source : this.languageSources) {
            lang.putAll(source);
        }

        for (var provider : providers) {
            lang.putAll(provider.getLangSource());
            replacements.putAll(provider.getLangReplacements());
        }

        for (var possibleKey : keys) {
            if (lang.containsKey(possibleKey)) {
                key = possibleKey;
                break;
            }
        }

        return ConfigUtil.iterateVariables("%", lang.getOrDefault(key, key), replacements, false);
    }

    public Component toComponent() {
        return MiniMessage.get().parse(toFormattedString());
    }

    public String toLegacyString() {
        return LegacyComponentSerializer.legacy(ChatColor.COLOR_CHAR).serialize(toComponent());
    }

    public String toString() {
        return MiniMessage.get().stripTokens(toFormattedString());
    }

    public static Lang key(String... key) {
        return new Lang(key);
    }

    public static Component simple(String... key) {
        return new Lang(key).toComponent();
    }

    public static String str(String... key) {
        return new Lang(key).toString();
    }

    public static String legacy(String... key) {
        return new Lang(key).toLegacyString();
    }

    public static void load(Plugin plugin) {
        try {
            var langFile = new File(plugin.getDataFolder(), "lang.yml");

            if (!langFile.exists()) plugin.saveResource("lang.yml", false);

            var langConfig = new YamlConfiguration();
            langConfig.load(langFile);

            langDictionary.putAll(ConfigUtil.toStringMap(langConfig, true));
        }
        catch (IOException | InvalidConfigurationException e) {
            Bolster.getInstance().getLogger().severe("Error loading lang file for plugin " + plugin.getName());
        }
    }
}
