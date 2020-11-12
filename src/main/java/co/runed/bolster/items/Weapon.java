package co.runed.bolster.items;

import co.runed.bolster.util.Category;
import co.runed.bolster.util.ItemBuilder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public abstract class Weapon extends Item
{
    public Weapon()
    {
        super();

        this.setAttackDamage(10);
        this.setAttackSpeed(10);

        this.addCategory(Category.WEAPONS);
    }

    @Override
    public ItemStack toItemStack()
    {
        ItemBuilder builder = new ItemBuilder(super.toItemStack());

        builder.addItemFlag(ItemFlag.HIDE_ATTRIBUTES);
        builder.addItemFlag(ItemFlag.HIDE_UNBREAKABLE);

        builder.setUnbreakable(true);

        return builder.build();
    }
}
