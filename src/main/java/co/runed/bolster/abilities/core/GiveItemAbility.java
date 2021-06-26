package co.runed.bolster.abilities.core;

import co.runed.bolster.abilities.TargetedAbility;
import co.runed.bolster.entity.BolsterEntity;
import co.runed.bolster.items.Item;
import co.runed.bolster.managers.ItemManager;
import co.runed.bolster.util.Definition;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class GiveItemAbility extends TargetedAbility<BolsterEntity>
{
    Definition<Item> itemDef;
    int outputCount = 1;

    public GiveItemAbility(Target<BolsterEntity> target, Definition<Item> itemDef, int outputCount)
    {
        super(target);

        this.itemDef = itemDef;
        this.outputCount = outputCount;
    }

    @Override
    public void onActivate(Properties properties)
    {
        LivingEntity entity = this.getTarget().get(properties).getBukkit();

        if (!(entity instanceof Player)) return;

        ItemManager.getInstance().giveItem(entity, ((Player) entity).getInventory(), this.itemDef, this.outputCount);
    }
}
