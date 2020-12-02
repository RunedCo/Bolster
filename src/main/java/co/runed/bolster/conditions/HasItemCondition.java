package co.runed.bolster.conditions;

import co.runed.bolster.entity.BolsterEntity;
import co.runed.bolster.items.Item;
import co.runed.bolster.managers.ItemManager;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.registries.Registries;
import co.runed.bolster.wip.target.Target;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class HasItemCondition extends TargetedCondition<BolsterEntity>
{
    String id;
    int count;

    public HasItemCondition(Target<BolsterEntity> target, Class<? extends Item> item, int count)
    {
        this(target, Registries.ITEMS.getId(item), count);
    }

    public HasItemCondition(Target<BolsterEntity> target, String id, int count)
    {
        super(target);
        this.id = id;
        this.count = count;
    }

    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        LivingEntity entity = this.getTarget().get(properties).getBukkit();

        if (!(entity instanceof Player)) return false;

        return ItemManager.getInstance().inventoryContainsAtLeast((Player) entity, this.id, this.count);
    }

    @Override
    public void onFail(IConditional conditional, Properties properties)
    {

    }
}
