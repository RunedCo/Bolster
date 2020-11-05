package co.runed.bolster.util;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;


public class ItemBuilder
{
    private ItemStack itemStack;

    public ItemBuilder(Material material)
    {
        this(new ItemStack(material));
    }

    public ItemBuilder(ItemStack itemStack)
    {
        this.itemStack = itemStack.clone();
    }

    public ItemBuilder addAllItemFlags()
    {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        this.itemStack.setItemMeta(meta);

        return new ItemBuilder(this.itemStack);
    }

    public ItemBuilder setDisplayName(String name)
    {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setDisplayName(name);
        this.itemStack.setItemMeta(meta);
        return new ItemBuilder(this.itemStack);
    }

    public ItemBuilder setColor(Color color)
    {
        ItemMeta itemMeta = this.itemStack.getItemMeta();

        if (!(itemMeta instanceof PotionMeta)) return new ItemBuilder(this.itemStack);

        PotionMeta meta = (PotionMeta) itemMeta;

        meta.setColor(color);
        this.itemStack.setItemMeta(meta);
        return new ItemBuilder(this.itemStack);
    }

    public ItemBuilder addLore(String line)
    {
        ItemMeta meta = this.itemStack.getItemMeta();
        List<String> lore = meta.getLore();

        if (lore == null)
        {
            lore = new ArrayList<>();
        }

        List<String> formatted = StringUtil.formatLore(line);

        lore.addAll(formatted);

        meta.setLore(lore);
        this.itemStack.setItemMeta(meta);
        return new ItemBuilder(this.itemStack);
    }

    public ItemBuilder setLore(List<String> lore)
    {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setLore(lore);
        this.itemStack.setItemMeta(meta);
        return new ItemBuilder(this.itemStack);
    }

    public ItemBuilder setLore(String lore)
    {
        if (lore == null) return new ItemBuilder(this.itemStack);

        return this.setLore(StringUtil.formatLore(lore));
    }

    public ItemBuilder addItemFlag(ItemFlag flag)
    {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.addItemFlags(flag);
        this.itemStack.setItemMeta(meta);
        return new ItemBuilder(this.itemStack);
    }

    public ItemBuilder setUnbreakable(boolean unbreakable)
    {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setUnbreakable(unbreakable);
        this.itemStack.setItemMeta(meta);
        return new ItemBuilder(this.itemStack);
    }

    public ItemBuilder setCustomModelData(int data)
    {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setCustomModelData(data);
        this.itemStack.setItemMeta(meta);
        return new ItemBuilder(this.itemStack);
    }

    public ItemBuilder hideName()
    {
        return this.setDisplayName(ChatColor.RESET + "");
    }

    public ItemBuilder addUnsafeEnchant(Enchantment ench, int level)
    {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.addEnchant(ench, level, true);
        this.itemStack.setItemMeta(meta);
        return new ItemBuilder(this.itemStack);
    }

    public ItemBuilder addAttributeModifier(Attribute attribute, AttributeModifier modifier)
    {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.addAttributeModifier(attribute, modifier);
        this.itemStack.setItemMeta(meta);
        return new ItemBuilder(this.itemStack);
    }

    public <T, Z> ItemBuilder setPersistentData(NamespacedKey key, PersistentDataType<T, Z> dataType, Z value)
    {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.getPersistentDataContainer().set(key, dataType, value);
        this.itemStack.setItemMeta(meta);
        return new ItemBuilder(this.itemStack);
    }

    public ItemStack build()
    {
        return this.itemStack;
    }

    public static boolean hasItemName(String name, ItemStack item)
    {
        if (item == null || item.getType() == Material.AIR)
        {
            return false;
        }

        if (item.hasItemMeta())
        {
            if (item.getItemMeta().hasDisplayName())
            {
                if (item.getItemMeta().getDisplayName().equals(name))
                {
                    return true;
                }
            }
        }

        return false;
    }

}