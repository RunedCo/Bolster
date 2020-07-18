package co.runed.bolster.abilities;

import co.runed.bolster.abilities.properties.AbilityProperties;
import co.runed.bolster.properties.Properties;
import co.runed.bolster.util.WorldUtil;
import net.minecraft.server.v1_16_R1.EntityLiving;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;

import java.util.List;
import java.util.Random;

public class ReplaceBlocksAbility extends Ability
{
    List<Material> materialsToReplace;
    List<Material> replaceWith;

    int range;
    int radiusUp;
    int radiusDown;
    int radiusHorizontal;

    Random random = new Random();

    public ReplaceBlocksAbility(List<Material> materialsToReplace, List<Material> replaceWith, int range, int radiusUp, int radiusDown, int radiusHorizontal)
    {
        super();

        this.materialsToReplace = materialsToReplace;
        this.replaceWith = replaceWith;

        this.range = range;
        this.radiusUp = radiusUp;
        this.radiusDown = radiusDown;
        this.radiusHorizontal = radiusHorizontal;
    }

    @Override
    public void onActivate(Properties properties)
    {
        LivingEntity caster = properties.get(AbilityProperties.CASTER);
        Location target = WorldUtil.getTargetBlock(caster, range).getLocation();

        int u = radiusUp;
        int d = radiusDown;
        int h = radiusHorizontal;

        int yOffset = 0;

        Block block;

        for (int y = target.getBlockY() - d + yOffset; y <= target.getBlockY() + u + yOffset; y++) {
            for (int x = target.getBlockX() - h; x <= target.getBlockX() + h; x++) {
                for (int z = target.getBlockZ() - h; z <= target.getBlockZ() + h; z++) {
                    block = target.getWorld().getBlockAt(x, y, z);
                    for (int i = 0; i < this.materialsToReplace.size(); i++) {
                        // If specific blocks are being replaced, skip if the block isn't replaceable.
                        if (!this.materialsToReplace.get(i).equals(block.getType())) continue;
                        // If all blocks are being replaced, skip if the block is already replaced.
                        if (replaceWith.contains(block.getType())) continue;

                        block.setType(replaceWith.get(this.random.nextInt(replaceWith.size())));
                        break;
                    }
                }
            }
        }
    }
}
