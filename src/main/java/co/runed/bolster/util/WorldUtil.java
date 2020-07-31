package co.runed.bolster.util;

import net.minecraft.server.v1_16_R1.MathHelper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.*;

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

    public static Collection<Entity> getEntitiesAroundPoint(Location location, double radius)
    {
        Collection<Entity> entities = location.getWorld().getNearbyEntities(location, radius, radius, radius);

        // Remove the entities that are within the box above but not actually in the sphere we defined with the radius and location
        // This code below could probably be replaced in Java 8 with a stream -> filter
        // Create an iterator so we can loop through the list while removing entries
        // If the entity is outside of the sphere...
        // Remove it
        entities.removeIf(entity -> entity.getLocation().distanceSquared(location) > radius * radius);

        return entities;
    }
}
