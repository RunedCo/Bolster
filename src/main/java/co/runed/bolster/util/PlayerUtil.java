package co.runed.bolster.util;

import co.runed.bolster.Bolster;
import net.minecraft.server.v1_15_R1.ChatMessageType;
import net.minecraft.server.v1_15_R1.IChatBaseComponent;
import net.minecraft.server.v1_15_R1.PacketPlayOutAnimation;
import net.minecraft.server.v1_15_R1.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlayerUtil {

    public static List<Player> getPlayersWithGamemode(GameMode mode) {
        List<Player> players = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if(player.getGameMode() == mode) players.add(player);
        }

        return players;
    }

    public static void setNickName(Player player, String nickname) {
        player.setDisplayName(nickname);
    }

    public static void sendPlayerToServer(Player player, String server) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        out.writeUTF("Connect");
        out.writeUTF(server);

        player.sendPluginMessage(Bolster.getInstance(), "BungeeCord", b.toByteArray());
    }

    public static void sendAllToServer(String server) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                PlayerUtil.sendPlayerToServer(player, server);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void sendActionBar(Player player, String message)
    {
        message = message.replaceAll("%player%", player.getDisplayName());
        message = ChatColor.translateAlternateColorCodes('&', message);
        IChatBaseComponent chatComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");

        CraftPlayer craftPlayer = (CraftPlayer) player;

        PacketPlayOutChat packet = new PacketPlayOutChat(chatComponent, ChatMessageType.GAME_INFO);
        craftPlayer.getHandle().playerConnection.sendPacket(packet);
    }

    public static void showScreenShake(Player player) {
        Location loc = player.getLocation();
        float playerYaw = loc.getYaw();
        float playerPitch = loc.getPitch();

        float yaw = playerYaw - 1f;
        float pitch = playerPitch - 1f;

        //PacketPlayOutAnimation packet = new PacketPlayOutAnimation(((CraftPlayer)player).getHandle(), 1);
        //((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);

        PacketPlayOutAnimation packet = new PacketPlayOutAnimation(((CraftPlayer)player).getHandle(), 3);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);

        //PacketPlayOutPosition packet = new PacketPlayOutPosition(0.0, 0.0, 0.0, yaw, pitch, teleportFlags, 0);
        //((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }
}
