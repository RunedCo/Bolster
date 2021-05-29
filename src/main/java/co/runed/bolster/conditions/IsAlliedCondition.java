package co.runed.bolster.conditions;

import co.runed.bolster.BolsterEntity;
import co.runed.bolster.classes.BolsterClass;
import co.runed.bolster.managers.EntityManager;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;

public class IsAlliedCondition extends Condition
{
    Target<BolsterEntity> source;
    Target<BolsterEntity> target;

    public IsAlliedCondition(Target<BolsterEntity> target)
    {
        this(Target.CASTER, target);
    }

    public IsAlliedCondition(Target<BolsterEntity> source, Target<BolsterEntity> target)
    {
        super();

        this.source = source;
        this.target = target;
    }

    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        return EntityManager.getInstance().areEntitiesAllied(source.get(properties).getBukkit(), target.get(properties).getBukkit());
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
