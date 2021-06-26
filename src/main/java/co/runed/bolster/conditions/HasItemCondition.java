package co.runed.bolster.conditions;

import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.entity.BolsterEntity;
import co.runed.bolster.items.Item;
import co.runed.bolster.managers.ItemManager;
import co.runed.bolster.util.Definition;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.registries.Registries;
import co.runed.bolster.util.target.Target;
import org.bukkit.GameMode;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class HasItemCondition extends TargetedCondition<BolsterEntity>
{
    String id;
    int count;

    public HasItemCondition(Target<BolsterEntity> target, Definition<Item> item, int count)
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

        if (this.id == null)
        {
            this.id = properties.get(AbilityProperties.ITEM).getId();
        }

        for (Inventory inv : BolsterEntity.from(entity).getInventories())
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
