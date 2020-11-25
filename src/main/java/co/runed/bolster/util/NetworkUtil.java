package co.runed.bolster.util;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.EncoderException;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;

public final class NetworkUtil
{
    /**
     * Sends a packet to the given player.
     *
     * @param player the player
     * @param packet the packet
     */
    public static void sendPacket(@Nonnull Player player, @Nonnull PacketContainer packet)
    {
        try
        {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        }
        catch (InvocationTargetException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends a packet to all players connected to the server.
     *
     * @param packet the packet
     */
    public static void broadcastPacket(@Nonnull PacketContainer packet)
    {
        ProtocolLibrary.getProtocolManager().broadcastServerPacket(packet);
    }

    /**
     * Sends a packet to each of the given players
     *
     * @param players the players
     * @param packet  the packet
     */
    public static void broadcastPacket(@Nonnull Iterable<Player> players, @Nonnull PacketContainer packet)
    {
        for (Player player : players)
        {
            sendPacket(player, packet);
        }
    }

    public static void writeVarInt(ByteBuf buf, int i)
    {
        while ((i & -128) != 0)
        {
            buf.writeByte(i & 127 | 128);
            i >>>= 7;
        }

        buf.writeByte(i);
    }

    public static void writeString(ByteBuf buf, String string)
    {
        writeString(buf, string, 32767);
    }

    public static void writeString(ByteBuf buf, String string, int i)
    {
        byte[] bs = string.getBytes(StandardCharsets.UTF_8);
        if (bs.length > i)
        {
            throw new EncoderException("String too big (was " + bs.length + " bytes encoded, max " + i + ")");
        }
        else
        {
            writeVarInt(buf, bs.length);
            buf.writeBytes(bs);
        }
    }
}
