package co.runed.bolster.util.target;

import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.util.BukkitUtil;
import org.bukkit.Location;

public class TargetBlockTarget extends Target<Location>
{
    public TargetBlockTarget(int range)
    {
        super("target_block", (properties -> BukkitUtil.getTargetBlock(properties.get(AbilityProperties.CASTER).getBukkit(), range).getLocation()));
    }
}
