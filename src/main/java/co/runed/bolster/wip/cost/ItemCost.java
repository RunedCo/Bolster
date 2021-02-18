package co.runed.bolster.wip.cost;

import co.runed.bolster.BolsterEntity;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.conditions.HasItemCondition;
import co.runed.bolster.items.Item;
import co.runed.bolster.managers.ItemManager;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import org.bukkit.GameMode;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ItemCost extends Cost
{
    Class<? extends Item> itemClass;
    int count;
    HasItemCondition condition;

    public ItemCost(int count)
    {
        this(null, count);
    }

    public ItemCost(Class<? extends Item> itemClass)
    {
        this(itemClass, 1);
    }

    public ItemCost(Class<? extends Item> itemClass, int count)
    {
        this.itemClass = itemClass;
        this.count = count;

        this.condition = new HasItemCondition(Target.CASTER, itemClass, count);
    }

    @Override
    public boolean evaluate(Properties properties)
    {
        return this.condition.evaluate(null, properties);

//        LivingEntity caster = properties.get(AbilityProperties.CASTER).getBukkit();
//
//        if (!(caster instanceof Player)) return false;
//
//        Player player = (Player) caster;
//
//        // If player is in creative don't remove items
//        if (player.getGameMode().equals(GameMode.CREATIVE)) return true;
//
//        Item item;
//
//        if (this.itemClass == null)
//        {
//            item = properties.get(AbilityProperties.ITEM);
//
//            if (item == null) return false;
//        }
//        else
//        {
//            item = ItemManager.getInstance().createItem(player, this.itemClass);
//        }
//
//        int count = this.count;
//
//        if (count == -1 && properties.contains(AbilityProperties.ITEM_STACK))
//        {
//            count = properties.get(AbilityProperties.ITEM_STACK).getAmount();
//        }
//
//        return ItemManager.getInstance().inventoryContainsAtLeast(player.getInventory(), item.getId(), count);
    }

    @Override
    public boolean run(Properties properties)
    {
        LivingEntity entity = properties.get(AbilityProperties.CASTER).getBukkit();

        // If player is in creative don't remove items
        if (entity instanceof Player && ((Player) entity).getGameMode() == GameMode.CREATIVE) return true;

        Item item;

        if (this.itemClass == null)
        {
            item = properties.get(AbilityProperties.ITEM);

            if (item == null) return false;
        }
        else
        {
            item = ItemManager.getInstance().createItem(entity, this.itemClass);
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
}
