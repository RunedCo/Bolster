package co.runed.bolster.util;

import co.runed.bolster.Bolster;
import co.runed.bolster.events.CustomCanDestroyBlockEvent;
import co.runed.bolster.events.CustomCanPlaceBlockEvent;
import co.runed.bolster.events.EntityTargetedEvent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
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
import org.bukkit.util.Vector;

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

    public static List<Entity> getEntitiesRadius(Location location, double radius)
    {
        Collection<Entity> entities = location.getWorld().getNearbyEntities(location, radius, radius, radius);

        entities.removeIf(entity -> {
            EntityTargetedEvent event = new EntityTargetedEvent(entity);
            Bukkit.getServer().getPluginManager().callEvent(event);

            return event.isCancelled();
        });

        return new ArrayList<>(entities);
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

    /**
     * @param location  starting position
     * @param radius    distance cone travels
     * @param degrees   angle of cone
     * @param direction direction of the cone
     * @return All entities inside the cone
     */
    public static List<Entity> getEntitiesInCone(Location location, float radius, float degrees, Vector direction)
    {
        List<Entity> newEntities = new ArrayList<>();        //    Returned list
        float squaredRadius = radius * radius;                     //    We don't want to use square root
        Vector startPos = location.toVector();

        Collection<Entity> entities = getEntitiesRadius(location, squaredRadius);

        for (Entity e : entities)
        {
            Vector relativePosition = e.getLocation().toVector();                            //    Position of the entity relative to the cone origin
            relativePosition.subtract(startPos);
            if (relativePosition.lengthSquared() > squaredRadius)
                continue;                    //    First check : distance
            if (getAngleBetweenVectors(direction, relativePosition) > degrees) continue;    //    Second check : angle

            newEntities.add(e);                                                                //    The entity e is in the cone
        }

        return newEntities;
    }

    /**
     * @param startPos  starting position
     * @param radius    distance cone travels
     * @param degrees   angle of cone
     * @param direction direction of the cone
     * @return All block positions inside the cone
     */
    public static List<Vector> getPositionsInCone(Vector startPos, float radius, float degrees, Vector direction)
    {
        List<Vector> positions = new ArrayList<>();        //    Returned list
        float squaredRadius = radius * radius;                     //    We don't want to use square root

        for (float x = startPos.getBlockX() - radius; x < startPos.getBlockX() + radius; x++)
            for (float y = startPos.getBlockY() - radius; y < startPos.getBlockY() + radius; y++)
                for (float z = startPos.getBlockZ() - radius; z < startPos.getBlockZ() + radius; z++)
                {
                    Vector relative = new Vector(x, y, z);
                    relative.subtract(startPos);
                    if (relative.lengthSquared() > squaredRadius) continue;            //    First check : distance
                    if (getAngleBetweenVectors(direction, relative) > degrees) continue;    //    Second check : angle

                    positions.add(new Vector(x, y, z));                                                //    The position v is in the cone
                }
        return positions;
    }

    public static float getAngleBetweenVectors(Vector v1, Vector v2)
    {
        return Math.abs((float) Math.toDegrees(v1.angle(v2)));
    }

    public static List<Entity> getEntitiesInFrontOf(LivingEntity source, float maxDistance)
    {
        List<Entity> output = new ArrayList<>();        //    Returned list
        Collection<Entity> entities = getEntitiesRadius(source.getLocation(), maxDistance);

        for (Entity entity : entities)
        {
            if (!isTargetBehindEntity(source, entity) && entity.getLocation().distance(source.getLocation()) <= maxDistance)
            {
                output.add(entity);
            }
        }

        return output;
    }

    private static boolean isTargetBehindEntity(Entity source, Entity target)
    {
        return isTargetBehindLocation(source.getLocation(), target);
    }

    private static boolean isTargetBehindLocation(Location source, Entity target)
    {
        double yaw = 2 * Math.PI - Math.PI * source.getYaw() / 180;
        Vector v = target.getLocation().toVector().subtract(source.toVector());

        Vector r = new Vector(Math.sin(yaw), 0, Math.cos(yaw));
        float theta = r.angle(v);

        return Math.PI / 2 < theta && theta < 3 * Math.PI / 2;
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

    public static Vector makeFinite(Vector vector)
    {
        double x = vector.getX();
        double y = vector.getY();
        double z = vector.getZ();

        if (Double.isNaN(x)) x = 0.0D;
        if (Double.isNaN(y)) y = 0.0D;
        if (Double.isNaN(z)) z = 0.0D;

        if (Double.isInfinite(x))
        {
            boolean negative = (x < 0.0D);
            x = negative ? -1 : 1;
        }

        if (Double.isInfinite(y))
        {
            boolean negative = (y < 0.0D);
            y = negative ? -1 : 1;
        }

        if (Double.isInfinite(z))
        {
            boolean negative = (z < 0.0D);
            z = negative ? -1 : 1;
        }

        return new Vector(x, y, z);
    }

    public static Location makeFinite(Location location)
    {
        float yaw = location.getYaw();
        float pitch = location.getPitch();

        if (Float.isNaN(yaw)) yaw = 0.0F;
        if (Float.isNaN(pitch)) pitch = 0.0F;

        if (Float.isInfinite(yaw))
        {
            boolean negative = (yaw < 0.0F);
            yaw = negative ? -1F : 1F;
        }

        if (Float.isInfinite(pitch))
        {
            boolean negative = (pitch < 0.0F);
            pitch = negative ? -1F : 1F;
        }

        Vector vec = makeFinite(location.toVector());

        return new Location(location.getWorld(), vec.getX(), vec.getY(), vec.getZ(), yaw, pitch);
    }

    public static Location lerp(Location start, Location end, double percent)
    {
        end = end.clone();
        start = start.clone();

        return (start.add((end.subtract(start)).multiply(percent)));
    }

    public static Vector lerp(Vector start, Vector end, double percent)
    {
        end = end.clone();
        start = start.clone();

        return (start.add((end.subtract(start)).multiply(percent)));
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

    public static List<LivingEntity> getLivingEntitiesFromUUIDs(Collection<UUID> uuids)
    {
        List<LivingEntity> entities = new ArrayList<>();

        for (UUID uuid : uuids)
        {
            Entity entity = Bukkit.getEntity(uuid);

            if (entity instanceof LivingEntity)
            {
                entities.add((LivingEntity) entity);
            }
        }

        return entities;
    }

    public static boolean canDestroyBlockAt(LivingEntity entity, Location location)
    {
        Block block = location.getBlock();

        CustomCanDestroyBlockEvent event = new CustomCanDestroyBlockEvent(block, entity.getEquipment().getItemInMainHand(), entity, true);
        Bukkit.getServer().getPluginManager().callEvent(event);

        return !event.isCancelled();
    }

    public static boolean canPlaceBlockAt(LivingEntity entity, Location location)
    {
        Block block = location.getBlock();

        CustomCanPlaceBlockEvent event = new CustomCanPlaceBlockEvent(block, entity.getEquipment().getItemInMainHand(), entity, true);
        Bukkit.getServer().getPluginManager().callEvent(event);

        return !event.isCancelled();
    }

    /**
     * Set the actionbar display for a player
     *
     * @param player  the player
     * @param message the text to display
     */
    public static void sendActionBar(Player player, String message)
    {
        message = message.replaceAll("%player%", player.getDisplayName());
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }
}
