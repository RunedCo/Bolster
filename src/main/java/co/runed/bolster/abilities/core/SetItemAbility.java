package co.runed.bolster.abilities.core;

import co.runed.bolster.BolsterEntity;
import co.runed.bolster.abilities.TargetedAbility;
import co.runed.bolster.items.Item;
import co.runed.bolster.managers.ItemManager;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.registries.Registries;
import co.runed.bolster.util.target.Target;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class SetItemAbility extends TargetedAbility<BolsterEntity>
{
    int slot;
    String itemId;
    int outputCount = 1;

    public SetItemAbility(Target<BolsterEntity> target, int slot, Class<? extends Item> itemClass, int outputCount)
    {
        this(target, slot, Registries.ITEMS.getId(itemClass), outputCount);
    }

    public SetItemAbility(Target<BolsterEntity> target, int slot, String itemId, int outputCount)
    {
        super(target);

        this.slot = slot;
        this.itemId = itemId;
        this.outputCount = outputCount;
    }

    @Override
    public void onActivate(Properties properties)
    {
        LivingEntity entity = this.getTarget().get(properties).getBukkit();

        if (!(entity instanceof Player)) return;

        ItemManager.getInstance().setItem(entity, ((Player) entity).getInventory(), this.slot, this.itemId, this.outputCount);
    }
}

