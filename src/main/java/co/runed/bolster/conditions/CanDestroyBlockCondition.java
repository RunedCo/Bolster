package co.runed.bolster.conditions;

import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.util.BukkitUtil;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public class CanDestroyBlockCondition extends TargetedCondition<Location>
{
    public CanDestroyBlockCondition(Target<Location> target)
    {
        super(target);
    }

    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        LivingEntity caster = properties.get(AbilityProperties.CASTER).getBukkit();
        Location location = this.getTarget().get(properties);

        return BukkitUtil.canDestroyBlockAt(caster, location);
    }

    @Override
    public void onFail(IConditional conditional, Properties properties, boolean inverted)
    {

    }

    @Override
    public String getErrorMessage(IConditional conditional, Properties properties, boolean inverted)
    {
        if (inverted) return null;

        return ChatColor.RED + "You cannot destroy that block!";
    }
}
