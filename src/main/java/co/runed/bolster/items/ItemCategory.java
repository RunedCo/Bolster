package co.runed.bolster.items;

import co.runed.bolster.managers.ItemManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemCategory {
    public static final ItemCategory WEAPONS = new ItemCategory("Weapons", null, new ItemStack(Material.DIAMOND_SWORD));

    public static final ItemCategory ALL = new ItemCategory("All Items", "Every item", new ItemStack(Material.CHEST));

    String name;
    String description;
    ItemStack icon;

    public ItemCategory(String name, String description, ItemStack icon) {
        this.name = name;
        this.description = description;

        ItemMeta meta = icon.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + name);
        List<String> lore = new ArrayList<>();
        if(this.description != null) lore.add(this.description);
        meta.setLore(lore);
        icon.setItemMeta(meta);

        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ItemStack getIcon() {


        return icon;
    }
}
