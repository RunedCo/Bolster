package co.runed.bolster.conditions;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.events.AbilityPlaceBlockEvent;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.wip.target.Target;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;

public class CanPlaceBlockCondition extends TargetedCondition<Location>
{
    public CanPlaceBlockCondition(Target<Location> target)
    {
        super(target);
    }

    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        LivingEntity caster = properties.get(AbilityProperties.CASTER).getBukkit();
        Location location = this.getTarget().get(properties);
        Block block = location.getBlock();
        Ability ability = conditional instanceof Ability ? (Ability) conditional : null;

        AbilityPlaceBlockEvent event = new AbilityPlaceBlockEvent(ability, block, block.getState(), block, caster.getEquipment().getItemInMainHand(), caster, true);
        Bukkit.getServer().getPluginManager().callEvent(event);

        return !event.isCancelled();
    }

    @Override
    public void onFail(IConditional conditional, Properties properties)
    {

    }
}
