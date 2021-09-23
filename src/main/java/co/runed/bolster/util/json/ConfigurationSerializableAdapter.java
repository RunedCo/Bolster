package co.runed.bolster.util.json;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.yaml.snakeyaml.Yaml;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigurationSerializableAdapter implements JsonSerializer<ConfigurationSerializable>, JsonDeserializer<ConfigurationSerializable> {
    final Type objectStringMapType = new TypeToken<Map<String, Object>>() {
    }.getType();

    Yaml yaml = new Yaml();

    @Override
    public ConfigurationSerializable deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Map<String, Object> map = new LinkedHashMap<>();

        if (json.isJsonObject()) {
            var jsonString = json.toString();

            map = yaml.load(jsonString);

            for (var entry : json.getAsJsonObject().entrySet()) {
                final var value = entry.getValue();
                final var name = entry.getKey();

                if (value.isJsonObject() && value.getAsJsonObject().has(ConfigurationSerialization.SERIALIZED_TYPE_KEY)) {
                    map.put(name, this.deserialize(value, value.getClass(), context));
                }
                else {
                    map.put(name, context.deserialize(value, Object.class));
                }
            }
        }

        return ConfigurationSerialization.deserializeObject(map);
    }

    @Override
    public JsonElement serialize(ConfigurationSerializable src, Type typeOfSrc, JsonSerializationContext context) {
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, ConfigurationSerialization.getAlias(src.getClass()));
        map.putAll(src.serialize());
        return context.serialize(map, objectStringMapType);
    }
}