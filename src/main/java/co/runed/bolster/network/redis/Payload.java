package co.runed.bolster.network.redis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class Payload
{
    public String sender = null;
    public String target = null;

    public String toJson()
    {
        Gson gson = new GsonBuilder().create();

        return gson.toJson(this);
    }

    public static Payload fromJson(String json)
    {
        return fromJson(json, Payload.class);
    }

    public static <T extends Payload> T fromJson(String json, Class<T> type)
    {
        Gson gson = new GsonBuilder().create();

        return gson.fromJson(json, type);
    }
}
