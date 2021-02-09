package co.runed.bolster.conditions;

import co.runed.bolster.BolsterEntity;
import co.runed.bolster.items.Item;
import co.runed.bolster.managers.ItemManager;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.registries.Registries;
import co.runed.bolster.util.target.Target;
import org.bukkit.GameMode;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Collection;

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

        if (entity instanceof Player && ((Player) entity).getGameMode() == GameMode.CREATIVE) return true;

        Collection<Inventory> invs = new ArrayList<>();
        if (entity instanceof Player) invs.add(((Player) entity).getInventory());
        invs.addAll(BolsterEntity.from(entity).getAdditionalInventories());

        for (Inventory inv : invs)
        {
            boolean contains = ItemManager.getInstance().inventoryContainsAtLeast(inv, this.id, this.count);

            if (contains) return true;
        }

        return false;
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
