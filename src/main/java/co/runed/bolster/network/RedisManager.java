package co.runed.bolster.network;

import co.runed.bolster.Bolster;
import co.runed.bolster.events.RedisMessageEvent;
import co.runed.bolster.util.BukkitUtil;
import co.runed.redismessaging.RedisChannels;
import co.runed.redismessaging.payload.Payload;
import org.bukkit.plugin.Plugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class RedisManager extends JedisPubSub
{
    private static RedisManager _instance;

    Plugin plugin;
    Jedis subRedis;
    Jedis pubRedis;

    public RedisManager(Plugin plugin, Jedis subRedis, Jedis pubRedis)
    {
        super();

        _instance = this;

        this.plugin = plugin;
        this.subRedis = subRedis;
        this.pubRedis = pubRedis;

        subRedis.subscribe(this, RedisChannels.REQUEST_SERVERS_RESPONSE, RedisChannels.REQUEST_PLAYER_DATA_RESPONSE);
    }

    @Override
    public void onMessage(String channel, String message)
    {
        Payload payload = Payload.fromJson(message, PayloadImpl.class);

        if (payload.target == null || !payload.target.equals(Bolster.getServerId())) return;

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
        payload.sender = Bolster.getServerId();
        payload.target = "proxy";

        pubRedis.publish(channel, payload.toJson());
    }

    public static RedisManager getInstance()
    {
        return _instance;
    }

    private static class PayloadImpl extends Payload
    {

    }
}
