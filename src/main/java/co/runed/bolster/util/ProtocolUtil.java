package co.runed.bolster.util;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;

public final class ProtocolUtil
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
}
