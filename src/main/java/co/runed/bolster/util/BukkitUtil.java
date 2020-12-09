package co.runed.bolster.util;

import co.runed.bolster.Bolster;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public class BukkitUtil
{
    /**
     * Get every player that have a specific gamemode
     *
     * @param mode the gamemode
     * @return
     */
    public static List<Player> getPlayersWithGameMode(GameMode mode)
    {
        List<Player> players = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers())
        {
            if (player.getGameMode() == mode) players.add(player);
        }

        return players;
    }

    public static void setNickName(Player player, String nickname)
    {
        player.setDisplayName(nickname);
    }

    /**
     * Send a specific player to a server
     *
     * @param player the player
     * @param server the server
     * @throws IOException
     */
    public static void sendPlayerToServer(Player player, String server) throws IOException
    {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        out.writeUTF("Connect");
        out.writeUTF(server);

        player.sendPluginMessage(Bolster.getInstance(), "BungeeCord", b.toByteArray());
    }

    /**
     * Send every online player to a server
     *
     * @param server the server
     */
    public static void sendAllToServer(String server)
    {
        for (Player player : Bukkit.getOnlinePlayers())
        {
            try
            {
                sendPlayerToServer(player, server);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Gets the block in front of an entity at a specific range, ignoring air
     *
     * @param entity the entity
     * @param range  the range in blocks
     * @return the block
     */
    public static Block getTargetBlock(LivingEntity entity, int range)
    {
        return entity.getTargetBlock(EnumSet.of(Material.AIR, Material.CAVE_AIR, Material.VOID_AIR), range);
    }

    public static Collection<Entity> getEntitiesRadius(Location location, double radius)
    {
        return location.getWorld().getNearbyEntities(location, radius, radius, radius);
    }

    public static Collection<Entity> getEntitiesRadiusCircle(Location location, double radius)
    {
        Collection<Entity> entities = getEntitiesRadius(location, radius);

        // Remove the entities that are within the box above but not actually in the sphere we defined with the radius and location
        // This code below could probably be replaced in Java 8 with a stream -> filter
        // Create an iterator so we can loop through the list while removing entries
        // If the entity is outside of the sphere...
        // Remove it
        entities.removeIf(entity -> entity.getLocation().distanceSquared(location) > radius * radius);

        return entities;
    }

    public static BoundingBox radiusBoundingBox(Location location, double radius)
    {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        return new BoundingBox(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius);
    }

    public static List<Block> getBlocksRadiusCircle(Location position, int radiusUp, int radiusDown, int radiusHorizontal)
    {
        List<Block> blocks = getBlocksRadius(position, radiusUp, radiusDown, radiusHorizontal);

        // Remove the blocks that are within the box above but not actually in the sphere we defined with the radius and location
        // This code below could probably be replaced in Java 8 with a stream -> filter
        // Create an iterator so we can loop through the list while removing entries
        // If the entity is outside of the sphere...
        // Remove it
        blocks.removeIf(block -> block.getLocation().distanceSquared(position) > radiusHorizontal * radiusHorizontal);

        return blocks;
    }

    public static List<Block> getBlocksRadius(Location position, int radiusUp, int radiusDown, int radiusHorizontal)
    {
        List<Block> blocks = new ArrayList<>();

        for (int y = position.getBlockY() - radiusDown; y <= position.getBlockY() + radiusUp; y++)
        {
            for (int x = position.getBlockX() - radiusHorizontal; x <= position.getBlockX() + radiusHorizontal; x++)
            {
                for (int z = position.getBlockZ() - radiusHorizontal; z <= position.getBlockZ() + radiusHorizontal; z++)
                {
                    Block block = position.getWorld().getBlockAt(x, y, z);

                    blocks.add(block);
                }
            }
        }

        return blocks;
    }

    public static Location stringToLocation(String locString)
    {
        String[] coords = locString.split(",");

        double x = Double.parseDouble(coords[0]);
        double y = Double.parseDouble(coords[1]);
        double z = Double.parseDouble(coords[2]);

        float yaw = coords.length > 3 ? Float.parseFloat(coords[3]) : 0;
        float pitch = coords.length > 4 ? Float.parseFloat(coords[4]) : 0;

        return new Location(Bukkit.getWorlds().get(0), x, y, z, yaw, pitch);
    }

    public static boolean addToInventory(Inventory inventory, ItemStack item, boolean stackExisting, boolean ignoreMaxStack)
    {
        int amt = item.getAmount();
        ItemStack[] items = Arrays.copyOf(inventory.getContents(), inventory.getSize());
        if (stackExisting)
        {
            for (ItemStack itemStack : items)
            {
                if (itemStack == null) continue;
                if (itemStack.getAmount() + amt <= itemStack.getMaxStackSize())
                {
                    itemStack.setAmount(itemStack.getAmount() + amt);
                    amt = 0;
                    break;
                }
                else
                {
                    int diff = itemStack.getMaxStackSize() - itemStack.getAmount();
                    itemStack.setAmount(itemStack.getMaxStackSize());
                    amt -= diff;
                }
            }
        }

        if (amt > 0)
        {
            for (int i = 0; i < items.length; i++)
            {
                if (items[i] != null) continue;
                if (amt > item.getMaxStackSize() && !ignoreMaxStack)
                {
                    items[i] = item.clone();
                    items[i].setAmount(item.getMaxStackSize());
                    amt -= item.getMaxStackSize();
                }
                else
                {
                    items[i] = item.clone();
                    items[i].setAmount(amt);
                    amt = 0;
                    break;
                }
            }
        }

        if (amt == 0)
        {
            inventory.setContents(items);
            return true;
        }

        return false;
    }
}
