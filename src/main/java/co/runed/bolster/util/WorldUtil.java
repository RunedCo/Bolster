package co.runed.bolster.util;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;

import java.util.EnumSet;

public class WorldUtil
{
    public static Block getTargetBlock(LivingEntity entity, int range)
    {
        return entity.getTargetBlock(EnumSet.of(Material.AIR, Material.CAVE_AIR, Material.VOID_AIR), range);
    }
}
