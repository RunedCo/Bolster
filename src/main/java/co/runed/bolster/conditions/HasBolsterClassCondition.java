package co.runed.bolster.conditions;

import co.runed.bolster.classes.BolsterClass;
import co.runed.bolster.entity.BolsterEntity;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.registries.Registries;
import co.runed.bolster.util.target.Target;

public class HasBolsterClassCondition extends TargetedCondition<BolsterEntity>
{
    String classId;

    public HasBolsterClassCondition(Target<BolsterEntity> target, Class<? extends BolsterClass> clazz)
    {
        this(target, "");

        this.classId = Registries.CLASSES.getId(clazz);
    }

    public HasBolsterClassCondition(Target<BolsterEntity> target, String classId)
    {
        super(target);

        this.classId = classId;
    }

    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        BolsterEntity entity = this.getTarget().get(properties);

        if (entity == null || entity.getBolsterClass() == null || this.classId == null) return false;

        return entity.getBolsterClass().getId().equals(this.classId);
    }

    @Override
    public void onFail(IConditional conditional, Properties properties)
    {

    }
}
