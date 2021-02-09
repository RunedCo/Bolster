package co.runed.bolster.abilities.core;

import co.runed.bolster.BolsterEntity;
import co.runed.bolster.abilities.TargetedAbility;
import co.runed.bolster.items.Item;
import co.runed.bolster.managers.ItemManager;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class GiveItemAbility extends TargetedAbility<BolsterEntity>
{
    Class<? extends Item> itemClass;
    int outputCount = 1;

    public GiveItemAbility(Target<BolsterEntity> target, Class<? extends Item> itemClass, int outputCount)
    {
        super(target);

        this.itemClass = itemClass;
        this.outputCount = outputCount;
    }

    @Override
    public void onActivate(Properties properties)
    {
        LivingEntity entity = this.getTarget().get(properties).getBukkit();

        if (!(entity instanceof Player)) return;

        ItemManager.getInstance().giveItem(entity, ((Player) entity).getInventory(), this.itemClass, this.outputCount);
    }
}
