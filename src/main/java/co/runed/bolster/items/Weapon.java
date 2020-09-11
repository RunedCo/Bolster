package co.runed.bolster.items;

import co.runed.bolster.util.Category;
import co.runed.bolster.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class Weapon extends Item
{
    double damage = 32;
    double attackSpeed = 10;

    public Weapon()
    {
        super();

        this.addCategory(Category.WEAPONS);
    }

    @Override
    public List<String> getLore()
    {
        List<String> lore = new ArrayList<>();

        if (this.damage > 0)
        {
            lore.add(ChatColor.GRAY + "Attack Damage: " + ChatColor.RED + this.damage);
        }

        if (this.attackSpeed > 0)
        {
            lore.add(ChatColor.GRAY + "Attack Speed: " + ChatColor.YELLOW + this.attackSpeed);
        }

        if (lore.size() > 0)
        {
            lore.add("");
        }

        lore.addAll(super.getLore());

        return lore;
    }

    @Override
    public ItemStack toItemStack()
    {
        ItemBuilder builder = new ItemBuilder(super.toItemStack());

        builder.addItemFlag(ItemFlag.HIDE_ATTRIBUTES);

        builder.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier("attack_damage", this.damage - 1, AttributeModifier.Operation.ADD_NUMBER));
        builder.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier("attack_speed", this.attackSpeed - 1, AttributeModifier.Operation.ADD_NUMBER));

        return builder.build();
    }
}
