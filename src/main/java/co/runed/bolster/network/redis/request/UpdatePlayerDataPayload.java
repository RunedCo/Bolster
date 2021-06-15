package co.runed.bolster.network.redis.request;

import co.runed.bolster.network.redis.RequestPayload;

import java.util.UUID;

public class UpdatePlayerDataPayload extends RequestPayload
{
    public UUID uuid;
    public String playerData;
}
