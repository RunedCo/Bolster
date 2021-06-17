package co.runed.bolster.managers;

import co.runed.bolster.common.redis.RedisChannels;
import co.runed.bolster.common.redis.payload.Payload;
import co.runed.bolster.events.RedisMessageEvent;
import co.runed.bolster.util.BukkitUtil;
import org.bukkit.plugin.Plugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.UUID;

public class RedisManager extends JedisPubSub
{
    private static RedisManager _instance;

    Plugin plugin;
    Jedis subRedis;
    Jedis pubRedis;

    String senderId = UUID.randomUUID().toString();

    public RedisManager(Plugin plugin, Jedis subRedis, Jedis pubRedis)
    {
        super();

        _instance = this;

        this.plugin = plugin;
        this.subRedis = subRedis;
        this.pubRedis = pubRedis;

        subRedis.subscribe(this, RedisChannels.REQUEST_SERVERS_RESPONSE, RedisChannels.REQUEST_PLAYER_DATA_RESPONSE, RedisChannels.REGISTER_SERVER_RESPONSE);
    }

    public void setSenderId(String senderId)
    {
        this.senderId = senderId;
    }

    public String getSenderId()
    {
        return senderId;
    }

    @Override
    public void onMessage(String channel, String message)
    {
        Payload payload = Payload.fromJson(message, PayloadImpl.class);

        if (payload.target == null || !payload.target.equals(getSenderId())) return;

        System.out.println("Channel " + channel + " has sent a message from " + payload.sender);

        BukkitUtil.triggerEventSync(new RedisMessageEvent(channel, message));
    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels)
    {
        System.out.println("Client is Subscribed to channel : " + channel);
        System.out.println("Client is Subscribed to " + subscribedChannels + " no. of channels");
    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels)
    {
        System.out.println("Client is Unsubscribed from channel : " + channel);
        System.out.println("Client is Subscribed to " + subscribedChannels + " no. of channels");
    }

    public void publish(String channel, Payload payload)
    {
        payload.sender = this.getSenderId();
        payload.target = "proxy";

        pubRedis.publish(channel, payload.toJson());
    }

    public String get(String key)
    {
        return pubRedis.get(key);
    }

    public void set(String key, String value)
    {
        pubRedis.set(key, value);
    }

    public static RedisManager getInstance()
    {
        return _instance;
    }

    private static class PayloadImpl extends Payload
    {

    }
}
