package co.runed.bolster.conditions;

import co.runed.bolster.entity.BolsterEntity;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import org.bukkit.Location;

public class IsUnderCoverCondition extends TargetedCondition<BolsterEntity>
{
    public IsUnderCoverCondition(Target<BolsterEntity> target)
    {
        super(target);
    }

    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        BolsterEntity entity = this.getTarget().get(properties);
        Location loc = entity.getEyeLocation().clone().add(0, 1, 0);

        while (loc.getY() < loc.getWorld().getMaxHeight())
        {
            if (loc.getWorld().getBlockAt(loc).getType().isSolid())
            {
                return true;
            }

            loc.add(0, 1, 0);
        }

        return false;
    }

    @Override
    public void onFail(IConditional conditional, Properties properties, boolean inverted)
    {

    }

    @Override
    public String getErrorMessage(IConditional conditional, Properties properties, boolean inverted)
    {
        return null;
    }
}
