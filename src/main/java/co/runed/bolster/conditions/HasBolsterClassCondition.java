package co.runed.bolster.conditions;

import co.runed.bolster.classes.BolsterClass;
import co.runed.bolster.entity.BolsterEntity;
import co.runed.bolster.util.Definition;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.registries.Registries;
import co.runed.bolster.util.target.Target;
import org.bukkit.ChatColor;

public class HasBolsterClassCondition extends TargetedCondition<BolsterEntity>
{
    String classId;

    public HasBolsterClassCondition(Target<BolsterEntity> target, Definition<BolsterClass> clazz)
    {
        this(target, Registries.CLASSES.getId(clazz));
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
    public void onFail(IConditional conditional, Properties properties, boolean inverted)
    {

    }

    @Override
    public String getErrorMessage(IConditional conditional, Properties properties, boolean inverted)
    {
        BolsterClass bolsterClass = Registries.CLASSES.get(classId).create();

        if (inverted) return ChatColor.RED + "You must not be a " + bolsterClass.getName() + " to use this ability!";

        return ChatColor.RED + "You must be a " + bolsterClass.getName() + " to use this ability!";
    }
}
