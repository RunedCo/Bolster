package co.runed.bolster.abilities.base;

import co.runed.bolster.util.BukkitUtil;
import co.runed.bolster.util.target.Target;
import org.bukkit.Location;

/**
 * A MultiTargetAbility that gets entities in a radius
 */
public class AOEAbility extends MultiTargetAbility
{
    Target<Location> target;
    double radius;

    public AOEAbility(Target<Location> target, double radius)
    {
        super(null);

        this.radius = radius;
        this.target = target;

        this.setEntityFunction((properties) -> BukkitUtil.getEntitiesRadius(this.target.get(properties), radius));
    }
}
