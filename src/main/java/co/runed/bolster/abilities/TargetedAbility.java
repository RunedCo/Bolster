package co.runed.bolster.abilities;

import co.runed.bolster.util.target.ITargeted;
import co.runed.bolster.util.target.Target;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.Location;

public abstract class TargetedAbility extends Ability implements ITargeted<Location>
{
    Target<Location> target;

    public TargetedAbility()
    {
        this(Target.CASTER_LOCATION);
    }

    public TargetedAbility(Target<Location> target)
    {
        super();

        this.target = target;
    }

    @Override
    public Target<Location> getTarget()
    {
        return this.target;
    }

    @Override
    public void setTarget(Target<Location> target)
    {
        this.target = target;
    }

    @Override
    public boolean canActivate(Properties properties)
    {
        properties.set(AbilityProperties.TARGET_LOCATION, this.target);

        return super.canActivate(properties);
    }
}
