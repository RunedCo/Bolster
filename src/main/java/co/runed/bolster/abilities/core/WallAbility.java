package co.runed.bolster.abilities.core;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.util.RandomCollection;
import co.runed.bolster.util.WorldUtil;
import co.runed.bolster.util.properties.Properties;
import net.minecraft.server.v1_16_R2.BlockUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Wall;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class WallAbility extends Ability
{
    int range;
    int width;
    int height;
    int depth;
    RandomCollection<Material> blocks;
    Set<Material> materialsToReplace;

    public WallAbility(int range, int width, int height, int depth, RandomCollection<Material> blocks, Set<Material> materialsToReplace) {
        super();

        this.range = range;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.blocks = blocks;
        this.materialsToReplace = materialsToReplace;
    }

    @Override
    public void onActivate(Properties properties)
    {
        Block block = WorldUtil.getTargetBlock(this.getCaster(), this.range);

        this.makeWall(this.getCaster(), block.getLocation(), this.getCaster().getLocation().getDirection());
    }

    private void makeWall(LivingEntity livingEntity, Location location, Vector direction) {
        if (blocks == null || blocks.isEmpty()) return;
        if (location == null || direction == null) return;

        Block target = location.getBlock();

        Location loc = target.getLocation();
        Vector dir = direction.clone();
        int wallWidth = Math.round(this.width);
        int wallHeight = Math.round(this.height);
        int yOffset = 0;

        List<Block> blockList = new ArrayList<>();

        if (Math.abs(dir.getX()) > Math.abs(dir.getZ())) {
            int depthDir = dir.getX() > 0 ? 1 : -1;
            for (int z = loc.getBlockZ() - (wallWidth / 2); z <= loc.getBlockZ() + (wallWidth / 2); z++) {
                for (int y = loc.getBlockY() + yOffset; y < loc.getBlockY() + wallHeight + yOffset; y++) {
                    for (int x = target.getX(); x < target.getX() + depth && x > target.getX() - depth; x += depthDir) {
                        blockList.add(livingEntity.getWorld().getBlockAt(x, y, z));
                    }
                }
            }
        } else {
            int depthDir = dir.getZ() > 0 ? 1 : -1;
            for (int x = loc.getBlockX() - (wallWidth / 2); x <= loc.getBlockX() + (wallWidth / 2); x++) {
                for (int y = loc.getBlockY() + yOffset; y < loc.getBlockY() + wallHeight + yOffset; y++) {
                    for (int z = target.getZ(); z < target.getZ() + depth && z > target.getZ() - depth; z += depthDir) {
                        blockList.add(livingEntity.getWorld().getBlockAt(x, y, z));
                    }
                }
            }
        }

        for (Block block : blockList)
        {
            if (materialsToReplace.contains(block.getType())) block.setType(this.blocks.next());
        }
    }
}
