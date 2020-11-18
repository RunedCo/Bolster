package co.runed.bolster.items;

import co.runed.bolster.util.Category;
import co.runed.bolster.util.ItemBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public abstract class Weapon extends Item
{
    @Override
    public void create(ConfigurationSection config)
    {
        super.create(config);

        this.setAttackDamage(config.getDouble(Item.ATTACK_DAMAGE_KEY, 10d));
        this.setAttackSpeed(config.getDouble(Item.ATTACK_SPEED_KEY, 10d));

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
