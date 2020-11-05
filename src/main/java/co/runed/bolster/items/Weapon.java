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
    double attackDamage = 10;
    double attackSpeed = 10;
    double knockBackResistance = 0;
    double knockBack = 0;
    double power = 0;

    public Weapon()
    {
        super();

        this.addCategory(Category.WEAPONS);
    }

    public void setAttackDamage(double attackDamage)
    {
        this.attackDamage = attackDamage;
    }

    public double getAttackDamage()
    {
        return attackDamage;
    }

    public void setAttackSpeed(double attackSpeed)
    {
        this.attackSpeed = attackSpeed;
    }

    public double getAttackSpeed()
    {
        return attackSpeed;
    }

    public void setPower(double power)
    {
        this.power = power;
    }

    public double getPower()
    {
        return power;
    }

    public void setKnockBackResistance(double knockBackResistance)
    {
        this.knockBackResistance = knockBackResistance;
    }

    public double getKnockBackResistance()
    {
        return knockBackResistance;
    }

    @Override
    public List<String> getLore()
    {
        List<String> lore = new ArrayList<>();

        if (this.attackDamage > 0)
        {
            lore.add(ChatColor.GRAY + "Attack Damage: " + ChatColor.YELLOW + this.attackDamage);
        }

        if (this.power > 0)
        {
            lore.add(ChatColor.GRAY + "Power: " + ChatColor.YELLOW + this.power);
        }

        if (this.attackSpeed > 0)
        {
            lore.add(ChatColor.GRAY + "Attack Speed: " + ChatColor.YELLOW + this.attackSpeed);
        }

        if (this.knockBack > 0)
        {
            lore.add(ChatColor.GRAY + "Knockback: " + ChatColor.YELLOW + this.knockBack);
        }

        if (this.knockBackResistance > 0)
        {
            lore.add(ChatColor.GRAY + "Knockback Resistance: " + ChatColor.YELLOW + this.knockBackResistance);
        }

        List<String> baseLore = super.getLore();

        if (baseLore.size() > 0)
        {
            lore.add("");
        }

        lore.addAll(baseLore);

        return lore;
    }

    @Override
    public ItemStack toItemStack()
    {
        ItemBuilder builder = new ItemBuilder(super.toItemStack());

        builder.addItemFlag(ItemFlag.HIDE_ATTRIBUTES);
        builder.addItemFlag(ItemFlag.HIDE_UNBREAKABLE);

        builder.setUnbreakable(true);

        builder.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier("attack_damage", this.attackDamage - 1, AttributeModifier.Operation.ADD_NUMBER));
        builder.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier("attack_speed", this.attackSpeed - 1, AttributeModifier.Operation.ADD_NUMBER));
        builder.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new AttributeModifier("knockback_resistance", this.knockBackResistance, AttributeModifier.Operation.ADD_NUMBER));
        builder.addAttributeModifier(Attribute.GENERIC_ATTACK_KNOCKBACK, new AttributeModifier("knockback", this.knockBack, AttributeModifier.Operation.ADD_NUMBER));

        return builder.build();
    }
}
