package co.runed.bolster.util.lang;

import co.runed.bolster.Bolster;
import co.runed.bolster.util.config.ConfigUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.HashMap;
import java.util.Map;

public class Lang {
    private String key = "invalid";
    private String value;
    private Map<String, String> replacements = new HashMap<>();

    public Lang(String... keys) {
        var lang = Bolster.getInstance().getLang();

        for (var key : keys) {
            if (lang.containsKey(key)) {
                this.key = key;
                break;
            }
        }

        this.value = lang.getOrDefault(key, key);
    }

    public Lang with(LangProvider langProvider) {
        return this.replaceAll(langProvider.getLangKeys());
    }

    public Lang replaceAll(Map<String, String> keys) {
        replacements.putAll(keys);

        return this;
    }

    public Lang replace(String key, String value) {
        var map = new HashMap<String, String>();
        map.put(key, value);

        return replaceAll(map);
    }

    public Lang replace(String key, Component value) {
        return replace(key, MiniMessage.get().serialize(value));
    }

    private String toFormattedString() {
        return ConfigUtil.iterateVariables("%", value, replacements);
    }

    public Component toComponent() {
        return MiniMessage.get().parse(toFormattedString());
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
}
