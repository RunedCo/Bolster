package co.runed.bolster.abilities.conditions;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.conditions.Condition;
import co.runed.bolster.conditions.IConditional;
import co.runed.bolster.items.Item;
import co.runed.bolster.managers.ItemManager;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.inventory.ItemStack;

public class ItemStackIsItemCondition extends Condition
{
    Class<? extends Item> item;

    public ItemStackIsItemCondition(Class<? extends Item> item)
    {
        this.item = item;
    }

    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        if (!properties.contains(AbilityProperties.ITEM_STACK)) return false;

        ItemStack stack = properties.get(AbilityProperties.ITEM_STACK);
        String id = Bolster.getItemRegistry().getId(item);

        if (id == null) return false;

        return id.equals(ItemManager.getInstance().getItemIdFromStack(stack));
    }

    @Override
    public void onFail(IConditional conditional, Properties properties)
    {

    }
}
