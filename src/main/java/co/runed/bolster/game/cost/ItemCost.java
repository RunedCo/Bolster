package co.runed.bolster.game.cost;

import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.conditions.HasItemCondition;
import co.runed.bolster.entity.BolsterEntity;
import co.runed.bolster.items.Item;
import co.runed.bolster.managers.ItemManager;
import co.runed.bolster.util.Definition;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import org.bukkit.GameMode;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ItemCost extends Cost
{
    Definition<Item> itemDef;
    int count;
    HasItemCondition condition;

    public ItemCost(int count)
    {
        this((Definition<Item>) null, count);
    }

    public ItemCost(Item item)
    {
        this(item, 1);
    }

    public ItemCost(Item item, int count)
    {
        this(item.getDefinition(), count);
    }

    public ItemCost(Definition<Item> itemDef)
    {
        this(itemDef, 1);
    }

    public ItemCost(Definition<Item> itemDef, int count)
    {
        this.itemDef = itemDef;
        this.count = count;

        this.condition = new HasItemCondition(Target.CASTER, itemDef, count);
    }

    @Override
    public boolean evaluate(Properties properties)
    {
        return this.condition.evaluate(null, properties);
    }

    @Override
    public boolean run(Properties properties)
    {
        LivingEntity entity = properties.get(AbilityProperties.CASTER).getBukkit();

        // If player is in creative don't remove items
        if (entity instanceof Player && ((Player) entity).getGameMode() == GameMode.CREATIVE) return true;

        Item item;

        if (this.itemDef == null)
        {
            item = properties.get(AbilityProperties.ITEM);

            if (item == null) return false;
        }
        else
        {
            item = ItemManager.getInstance().createItem(entity, this.itemDef);
        }

        int count = this.count;

        if (count == -1 && properties.contains(AbilityProperties.ITEM_STACK))
        {
            count = properties.get(AbilityProperties.ITEM_STACK).getAmount();
        }

        for (Inventory inv : BolsterEntity.from(entity).getInventories())
        {
            boolean success = ItemManager.getInstance().removeItem(inv, item, count);

            if (success) return true;
        }

        return false;
    }

    @Override
    public String getErrorMessage(Properties properties)
    {
        return null;
    }
}
