package co.runed.bolster.conditions;

import co.runed.bolster.managers.UpgradeManager;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import co.runed.bolster.wip.upgrade.Upgrade;
import org.bukkit.entity.LivingEntity;

public class HasUpgradeCondition extends TargetedCondition<LivingEntity>
{
    Upgrade upgrade;
    int minLevel;

    public HasUpgradeCondition(Target<LivingEntity> target, Upgrade upgrade)
    {
        this(target, upgrade, 1);
    }

    public HasUpgradeCondition(Target<LivingEntity> target, Upgrade upgrade, int minLevel)
    {
        super(target);

        this.upgrade = upgrade;
        this.minLevel = minLevel;
    }

    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        return UpgradeManager.getInstance().hasUpgrade(this.getTarget().get(properties), this.upgrade, this.minLevel);
    }

    @Override
    public void onFail(IConditional conditional, Properties properties, boolean inverted)
    {

    }
}
