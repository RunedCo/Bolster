package co.runed.bolster.conditions;

import co.runed.bolster.conditions.base.Condition;
import co.runed.bolster.entity.BolsterEntity;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import co.runed.bolster.wip.InventoryTracker;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;

public class IsInventoryOpenCondition extends Condition
{
    Target<BolsterEntity> target;
    InventoryType inventoryType = null;

    public IsInventoryOpenCondition(Target<BolsterEntity> target)
    {
        this(target, null);
    }

    public IsInventoryOpenCondition(Target<BolsterEntity> target, InventoryType inventoryType)
    {
        super();

        this.target = target;
        this.inventoryType = inventoryType;
    }

    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        LivingEntity entity = this.target.get(properties).getBukkit();

        if (!(entity instanceof Player)) return false;

        Player player = (Player) entity;
        InventoryView inventory = player.getOpenInventory();

        if (!InventoryTracker.isInventoryOpen(player)) return false;

        return inventoryType == null || inventory.getType() == inventoryType;
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
