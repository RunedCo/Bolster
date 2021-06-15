package co.runed.bolster.network.redis.request;

import co.runed.bolster.network.redis.RequestPayload;

public class RegisterServerPayload extends RequestPayload
{
    public String name;
    public String serverId;
    public String gameMode;

    public String ipAddress;
    public int port;

    public String status;
}
