package co.runed.bolster.util;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class InventoryUtil
{
    public static boolean addToInventory(Inventory inventory, ItemStack item, boolean stackExisting, boolean ignoreMaxStack)
    {
        int amt = item.getAmount();
        ItemStack[] items = Arrays.copyOf(inventory.getContents(), inventory.getSize());
        if (stackExisting)
        {
            for (ItemStack itemStack : items)
            {
                if (itemStack == null) continue;
                if (itemStack.getAmount() + amt <= itemStack.getMaxStackSize())
                {
                    itemStack.setAmount(itemStack.getAmount() + amt);
                    amt = 0;
                    break;
                }
                else
                {
                    int diff = itemStack.getMaxStackSize() - itemStack.getAmount();
                    itemStack.setAmount(itemStack.getMaxStackSize());
                    amt -= diff;
                }
            }
        }

        if (amt > 0)
        {
            for (int i = 0; i < items.length; i++)
            {
                if (items[i] != null) continue;
                if (amt > item.getMaxStackSize() && !ignoreMaxStack)
                {
                    items[i] = item.clone();
                    items[i].setAmount(item.getMaxStackSize());
                    amt -= item.getMaxStackSize();
                }
                else
                {
                    items[i] = item.clone();
                    items[i].setAmount(amt);
                    amt = 0;
                    break;
                }
            }
        }

        if (amt == 0)
        {
            inventory.setContents(items);
            return true;
        }

        return false;
    }
}
