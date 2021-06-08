package co.runed.bolster.util.json;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.io.IOException;
import java.time.ZonedDateTime;

public class GsonUtil
{
    public static Gson create()
    {
        ExclusionStrategy excludeStrategy = new ExclusionStrategy()
        {
            @Override
            public boolean shouldSkipClass(Class<?> clazz)
            {
                return false;
            }

            @Override
            public boolean shouldSkipField(FieldAttributes field)
            {
                return field.getAnnotation(JsonExclude.class) != null;
            }
        };

        return new GsonBuilder()
                .setExclusionStrategies(excludeStrategy)
                .registerTypeAdapter(ZonedDateTime.class, new TypeAdapter<ZonedDateTime>()
                {
                    @Override
                    public void write(JsonWriter out, ZonedDateTime value) throws IOException
                    {
                        out.value(value.toString());
                    }

                    @Override
                    public ZonedDateTime read(JsonReader in) throws IOException
                    {
                        return ZonedDateTime.parse(in.nextString());
                    }
                })
                .registerTypeHierarchyAdapter(ConfigurationSerializable.class, new ConfigurationSerializableAdapter())
                .enableComplexMapKeySerialization()
                .create();
    }
}
