package co.runed.bolster.abilities.core;

import co.runed.bolster.abilities.TargetedAbility;
import co.runed.bolster.util.RandomCollection;
import co.runed.bolster.util.WorldUtil;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.wip.target.Target;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import java.util.Collection;

public class ReplaceBlocksAbility extends TargetedAbility<Location>
{
    Collection<Material> materialsToReplace;
    RandomCollection<Material> replaceWith;

    int yOffset;
    int range;
    int radiusUp;
    int radiusDown;
    int radiusHorizontal;

    public ReplaceBlocksAbility(Target<Location> target, Collection<Material> materialsToReplace, RandomCollection<Material> replaceWith, int yOffset, int range, int radiusUp, int radiusDown, int radiusHorizontal)
    {
        super(target);

        this.materialsToReplace = materialsToReplace;
        this.replaceWith = replaceWith;

        this.yOffset = yOffset;

        this.range = range;
        this.radiusUp = radiusUp;
        this.radiusDown = radiusDown;
        this.radiusHorizontal = radiusHorizontal;
    }

    @Override
    public void onActivate(Properties properties)
    {
        Location target = this.getTarget().get(properties);
        target.add(0, yOffset, 0);

        for (Block block : WorldUtil.getBlocksRadius(target, radiusUp, radiusDown, radiusHorizontal))
        {
            for (Material material : this.materialsToReplace)
            {
                // If specific blocks are being replaced, skip if the block isn't replaceable.
                if (!material.equals(block.getType())) continue;
                // If all blocks are being replaced, skip if the block is already replaced.
                if (replaceWith.contains(block.getType())) continue;

                Material next = replaceWith.next();
                BlockData data = block.getBlockData();
                BlockData newBlockData = Bukkit.createBlockData(data.getAsString().replaceAll(block.getType().getKey().getKey(), next.getKey().getKey()));

                block.setType(next);
                block.setBlockData(newBlockData);
                break;
            }
        }
    }
}
