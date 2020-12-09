package co.runed.bolster.conditions;

import co.runed.bolster.entity.BolsterEntity;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import org.bukkit.entity.EntityType;

public class IsEntityTypeCondition extends TargetedCondition<BolsterEntity>
{
    EntityType type;

    public IsEntityTypeCondition(Target<BolsterEntity> target, EntityType type)
    {
        super(target);

        this.type = type;
    }

    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        return this.getTarget().get(properties).getType() == EntityType.PLAYER;
    }

    @Override
    public void onFail(IConditional conditional, Properties properties)
    {

    }
}
