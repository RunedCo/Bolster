package co.runed.bolster.util;

import co.runed.bolster.Bolster;
import net.minecraft.server.v1_16_R1.*;
import org.bukkit.*;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayerUtil {

    public static List<Player> getPlayersWithGamemode(GameMode mode) {
        List<Player> players = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getGameMode() == mode) players.add(player);
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

    public static void sendActionBar(Player player, String message) {
        message = message.replaceAll("%player%", player.getDisplayName());
        message = ChatColor.translateAlternateColorCodes('&', message);
        IChatBaseComponent chatComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");

        CraftPlayer craftPlayer = (CraftPlayer) player;

        PacketPlayOutChat packet = new PacketPlayOutChat(chatComponent, ChatMessageType.GAME_INFO, player.getUniqueId());
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

        PacketPlayOutAnimation packet = new PacketPlayOutAnimation(((CraftPlayer) player).getHandle(), 3);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);

        //PacketPlayOutPosition packet = new PacketPlayOutPosition(0.0, 0.0, 0.0, yaw, pitch, teleportFlags, 0);
        //((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public static void dropItem(Player player, ItemStack itemStack) {
        Random random = new Random();
        World world = player.getWorld();

        Location spawnLoc = player.getLocation().clone();
        spawnLoc.setY(player.getEyeLocation().getY() - 0.30000001192092896D);

        Item item = (Item) world.spawnEntity(spawnLoc, EntityType.DROPPED_ITEM);
        item.setItemStack(itemStack);
        item.setPickupDelay(40);

        float pitch = player.getEyeLocation().getPitch();
        float yaw = player.getEyeLocation().getYaw();

        float f = 0.3F;
        float f1 = MathHelper.sin(pitch * 0.017453292F);
        float f2 = MathHelper.cos(pitch * 0.017453292F);
        float f3 = MathHelper.sin(yaw * 0.017453292F);
        float f4 = MathHelper.cos(yaw * 0.017453292F);
        float f5 = random.nextFloat() * 6.2831855F;
        float f6 = 0.02F * random.nextFloat();
        item.setVelocity(new Vector((double) (-f3 * f2 * 0.3F) + Math.cos(f5) * (double) f6,
                (-f1 * 0.3F + 0.1F + (random.nextFloat() - random.nextFloat()) * 0.1F),
                (double) (f4 * f2 * 0.3F) + Math.sin(f5) * (double) f6));
    }
}
