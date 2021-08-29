package co.runed.bolster.util.json;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.BaseConstructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Modified version of Gson's default Object {@link TypeAdapter}.
 * <p>
 * Bukkit's serialization API is built around Yaml, and specifically SnakeYaml. Objects that are deserialized via
 * Bukkit's serialization API may have strict expectations regarding the types of data they encounter during
 * deserialization. This also applies to some of Bukkit's built-in serializable types, such as ItemMeta. For
 * compatibility reasons, this {@link TypeAdapter} is supposed to produce types that more closely match those produced
 * by SnakeYaml. Most notable differences to Gson's default types are:
 * <ul>
 * <li>We produce LinkedHashMap's instead of Gson's LinkedTreeMap.
 * <li>We delegate the parsing of numbers to SnakeYaml, which produces integers, longs, or BigInteger for whole numbers
 * and doubles for fractional numbers, whereas Gson always produces doubles by default.
 * <li>Gson represents special numbers like {@link Double#NaN} or infinities as Strings, but doesn't automatically
 * return them as double anymore. We check if a loaded String can be parsed as one of these special numbers, and then
 * return this number instead.
 * </ul>
 * <p>
 * Gson does not allow the default Object TypeAdapter to be overridden. This TypeAdapter therefore has to be explicitly
 * invoked whenever it is supposed to be used.
 * <p>
 * Another alternative could be to deserialize objects from Json via a Yaml parser: Since Json is supposed to be a
 * subset of Yaml, this should work in principal. However, there is no guarantee that the produced Json actually
 * conforms to standard Json: For instance, Json does not support special numbers like {@link Double#NaN} or infinities.
 * Their representation is therefore specific to the used Json generator and there is no guarantee that a certain Yaml
 * parser is then able to parse those numbers correctly.
 * <p>
 * There are also other limitations that make Json less suited for the serialization of {@link ConfigurationSerializable
 * ConfigurationSerializables}: See {@link BukkitAwareObjectTypeAdapter}.
 */
public class YamlLikeObjectTypeAdapter extends TypeAdapter<Object> {
    private static final ThreadLocal<Yaml> YAML = ThreadLocal.withInitial(() -> {
        var yamlOptions = new DumperOptions();
        yamlOptions.setIndent(2);
        yamlOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Representer yamlRepresenter = new YamlRepresenter();
        yamlRepresenter.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        BaseConstructor yamlConstructor = new YamlConstructor();
        return new Yaml(yamlConstructor, yamlRepresenter, yamlOptions);
    });

    public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
        @SuppressWarnings("unchecked")
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            // This is not actually expected to work currently, since Gson doesn't allow its default Object TypeAdapter
            // to be overridden yet.
            if (type.getRawType() == Object.class) {
                return (TypeAdapter<T>) YamlLikeObjectTypeAdapter.create(gson);
            }
            return null;
        }
    };

    public static TypeAdapter<Object> create(Gson gson) {
        return new YamlLikeObjectTypeAdapter(gson);
    }

    private final Gson gson;

    protected YamlLikeObjectTypeAdapter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public Object read(JsonReader in) throws IOException {
        var token = in.peek();
        switch (token) {
            case BEGIN_ARRAY:
                List<Object> list = new ArrayList<Object>();
                in.beginArray();
                while (in.hasNext()) {
                    // This recursively uses this custom Object TypeAdapter:
                    list.add(this.read(in));
                }
                in.endArray();
                return list;
            case BEGIN_OBJECT:
                // We use a LinkedHashMap instead of Gson's LinkedTreeMap:
                Map<String, Object> map = new LinkedHashMap<String, Object>();
                in.beginObject();
                while (in.hasNext()) {
                    // This recursively uses this custom Object TypeAdapter:
                    map.put(in.nextName(), this.read(in));
                }
                in.endObject();
                return map;
            case STRING:
                var string = in.nextString();
                if (in.isLenient()) {
                    // Check if we can parse the String as one of the special numbers (NaN and infinities):
                    try {
                        var number = Double.parseDouble(string);
                        if (!Double.isFinite(number)) {
                            return number;
                        } // Finite numbers are not expected to be represented as String
                    }
                    catch (NumberFormatException e) {
                    }
                }
                return string;
            case NUMBER:
                // We delegate the number parsing to the Yaml parser:
                var number = in.nextDouble();

                if (Math.rint(number) == number && !Double.isInfinite(number)) {
                    return (int) number;
                }

                return number;
            case BOOLEAN:
                return in.nextBoolean();
            case NULL:
                in.nextNull();
                return null;
            default:
                throw new IllegalStateException();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void write(JsonWriter out, Object value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }

        // Check if we find a more specific and therefore better suited TypeAdapter for the given object:
        var typeAdapter = (TypeAdapter<Object>) gson.getAdapter(value.getClass());
        // If there is no TypeAdapter registered that is more specific than Object, and if this custom Object
        // TypeAdapter has been registered and was able to replace Gson's default Object TypeAdapter (which is
        // not expected to be the case currently), we break the cycle and output an empty object:
        if (typeAdapter instanceof YamlLikeObjectTypeAdapter) {
            out.beginObject();
            out.endObject();
            return;
        }

        // Delegate to the found TypeAdapter:
        typeAdapter.write(out, value);
    }
}
