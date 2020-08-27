package co.runed.bolster.abilities.core;

import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.abilities.TargetedAbility;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.RandomCollection;
import co.runed.bolster.util.WorldUtil;
import co.runed.bolster.util.target.Target;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;

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
        LivingEntity caster = properties.get(AbilityProperties.CASTER);
        Location target = this.getTarget().get(properties);

        int u = radiusUp;
        int d = radiusDown;
        int h = radiusHorizontal;

        Block block;

        for (int y = target.getBlockY() - d + yOffset; y <= target.getBlockY() + u + yOffset; y++)
        {
            for (int x = target.getBlockX() - h; x <= target.getBlockX() + h; x++)
            {
                for (int z = target.getBlockZ() - h; z <= target.getBlockZ() + h; z++)
                {
                    block = target.getWorld().getBlockAt(x, y, z);

                    for (Material material : this.materialsToReplace)
                    {
                        // If specific blocks are being replaced, skip if the block isn't replaceable.
                        if (!material.equals(block.getType())) continue;
                        // If all blocks are being replaced, skip if the block is already replaced.
                        if (replaceWith.contains(block.getType())) continue;

                        block.setType(replaceWith.next());
                        break;
                    }
                }
            }
        }
    }
}
