package co.runed.bolster.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

public class WorldUtil
{
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
}
