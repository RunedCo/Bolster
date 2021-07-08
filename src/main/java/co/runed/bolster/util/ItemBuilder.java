package co.runed.bolster.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.stream.Collectors;


public class ItemBuilder
{
    private final ItemStack itemStack;

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
        return this.addItemFlags(EnumSet.allOf(ItemFlag.class));
    }

    public ItemBuilder addItemFlags(Collection<ItemFlag> flags)
    {
        ItemBuilder builder = this;

        for (ItemFlag flag : flags)
        {
            builder = builder.addItemFlag(flag);
        }

        return builder;
    }

    public ItemBuilder setMaterial(Material material)
    {
        this.itemStack.setType(material);

        return new ItemBuilder(this.itemStack);
    }

    public ItemBuilder setDisplayName(Component name)
    {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.displayName(name);
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

    public ItemBuilder addLoreComponent(Component line)
    {
        ItemMeta meta = this.itemStack.getItemMeta();
        List<Component> lore = meta.lore();

        if (lore == null)
        {
            lore = new ArrayList<>();
        }

//        List<String> formatted = StringUtil.formatLore(line);
//
//        lore.addAll(formatted);

        lore.add(line);

        meta.lore(lore);
        this.itemStack.setItemMeta(meta);
        return new ItemBuilder(this.itemStack);
    }

    public ItemBuilder addLoreComponent(Collection<Component> lore)
    {
        ItemBuilder builder = new ItemBuilder(this.itemStack);

        for (Component line : lore)
        {
            builder = builder.addLoreComponent(line);
        }

        return builder;
    }

    public ItemBuilder addLoreComponent(List<String> lore)
    {
        ItemBuilder builder = new ItemBuilder(this.itemStack);

        for (String line : lore)
        {
            builder = builder.addLoreComponent(Component.text(line));
        }

        return builder;
    }

//    public ItemBuilder setLoreComponent(Collection<String> lore)
//    {
//        List<Component> components = lore.stream().map(Component::text).collect(Collectors.toList());
//
//        return this.setLoreComponent(components);
//    }

    public ItemBuilder setLoreComponent(Collection<Component> lore)
    {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.lore(lore.stream().toList());
        this.itemStack.setItemMeta(meta);
        return new ItemBuilder(this.itemStack);
    }

    public ItemBuilder setLoreComponent(Component lore)
    {
        return this.setLoreComponent(Collections.singletonList(lore));
    }

    public ItemBuilder addBullet(String line)
    {
        return this.addBullet(Collections.singletonList(line));
    }

    public ItemBuilder addBullet(Collection<String> lore)
    {
        ItemBuilder builder = new ItemBuilder(this.itemStack);

        for (String line : lore)
        {
            builder = builder.addLore(StringUtil.formatBullet(line));
        }

        return builder;
    }

    public ItemBuilder addLore(String line)
    {
        List<String> formatted = StringUtil.formatLore(line);
        return this.addLoreComponent(formatted);
    }

    public ItemBuilder addLore(Collection<String> lore)
    {
        ItemBuilder builder = new ItemBuilder(this.itemStack);

        for (String line : lore)
        {
            builder = builder.addLore(line);
        }

        return builder;
    }

    public ItemBuilder setLore(List<String> lore)
    {
        return this.setLoreComponent(lore.stream().map(Component::text).collect(Collectors.toList()));
    }

    public ItemBuilder setLore(String lore)
    {
        if (lore == null) return new ItemBuilder(this.itemStack);

        return this.setLore(StringUtil.formatLore(lore));
    }

    public ItemBuilder addItemFlag(ItemFlag... flag)
    {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.addItemFlags(flag);
        this.itemStack.setItemMeta(meta);
        return new ItemBuilder(this.itemStack);
    }

    public ItemBuilder removeItemFlag(ItemFlag flag)
    {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.removeItemFlags(flag);
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

    public ItemBuilder setAmount(int amount)
    {
        this.itemStack.setAmount(amount);
        return new ItemBuilder(this.itemStack);
    }

    public ItemBuilder hideName()
    {
        return this.setDisplayName(Component.empty());
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

    public ItemBuilder removePersistentData(NamespacedKey key)
    {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.getPersistentDataContainer().remove(key);
        this.itemStack.setItemMeta(meta);
        return new ItemBuilder(this.itemStack);
    }

    public ItemBuilder setDamagePercent(double percent)
    {
        return this.setDamage((int) (this.itemStack.getType().getMaxDurability() * percent));
    }

    public ItemBuilder setDamage(int damage)
    {
        if (!(this.itemStack.getItemMeta() instanceof Damageable)) return this;
        Damageable damageable = (Damageable) this.itemStack.getItemMeta();

        damageable.setDamage(damage);

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
                return item.getItemMeta().getDisplayName().equals(name);
            }
        }

        return false;
    }

}