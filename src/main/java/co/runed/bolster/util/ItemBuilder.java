package co.runed.bolster.util;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;


public class ItemBuilder {

    private ItemStack item;

    public ItemBuilder(ItemStack itemstack) {
        this.item = itemstack;
    }

    public ItemBuilder addAllItemFlags() {
        ItemMeta meta = this.item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        this.item.setItemMeta(meta);

        return new ItemBuilder(this.item);
    }

    public ItemBuilder setDisplayName(String name) {
        ItemMeta meta = this.item.getItemMeta();
        meta.setDisplayName(name);
        this.item.setItemMeta(meta);
        return new ItemBuilder(this.item);
    }

    public ItemBuilder addLore(String line) {
        ItemMeta meta = this.item.getItemMeta();
        meta.getLore().add(line);
        this.item.setItemMeta(meta);
        return new ItemBuilder(this.item);
    }

    public ItemBuilder setLore(List<String> lore) {
        ItemMeta meta = this.item.getItemMeta();
        meta.setLore(lore);
        this.item.setItemMeta(meta);
        return new ItemBuilder(this.item);
    }

    public ItemBuilder setLore(String lore) {
        if (lore == null) return new ItemBuilder(this.item);

        return this.setLore(StringUtil.formatLore(lore));
    }

    public ItemBuilder addItemFlag(ItemFlag flag) {
        ItemMeta meta = this.item.getItemMeta();
        meta.addItemFlags(flag);
        this.item.setItemMeta(meta);
        return new ItemBuilder(this.item);
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        ItemMeta meta = this.item.getItemMeta();
        if(unbreakable) {
            meta.setUnbreakable(true);
        } else {
            meta.setUnbreakable(false);
        }

        this.item.setItemMeta(meta);
        return new ItemBuilder(this.item);
    }

    public ItemBuilder setCustomModelData(int data) {
        ItemMeta meta = this.item.getItemMeta();
        meta.setCustomModelData(data);
        this.item.setItemMeta(meta);
        return new ItemBuilder(this.item);
    }

    public ItemBuilder addUnsafeEnchant(Enchantment ench, int level) {
        ItemMeta meta = this.item.getItemMeta();
        meta.addEnchant(ench, level, true);
        this.item.setItemMeta(meta);
        return new ItemBuilder(this.item);
    }

    public <T, Z> ItemBuilder setPersistentData(NamespacedKey key, PersistentDataType<T, Z> dataType, Z value) {
        ItemMeta meta = this.item.getItemMeta();
        meta.getPersistentDataContainer().set(key, dataType, value);
        this.item.setItemMeta(meta);
        return new ItemBuilder(this.item);
    }

    public ItemStack build() {
        return this.item;
    }

    public static boolean hasItemName(String name, ItemStack item) {
        if(item == null || item.getType() == Material.AIR) {
            return false;
        }

        if(item.hasItemMeta()) {
            if(item.getItemMeta().hasDisplayName()) {
                if(item.getItemMeta().getDisplayName().equals(name)) {
                    return true;
                }
            }
        }

        return false;
    }

}