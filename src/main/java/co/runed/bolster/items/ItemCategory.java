package co.runed.bolster.items;

import co.runed.bolster.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemCategory {
    public static final ItemCategory WEAPONS = new ItemCategory("Weapons", null, new ItemStack(Material.DIAMOND_SWORD));
    public static final ItemCategory ALL = new ItemCategory("All Items", ChatColor.LIGHT_PURPLE + "Every item", new ItemStack(Material.CHEST));

    String name;
    String description;
    ItemStack icon;

    public ItemCategory(String name, String description, ItemStack icon) {
        this.name = name;
        this.description = description != null ? description : "";

        this.icon = new ItemBuilder(icon)
                .setDisplayName(ChatColor.BOLD + "" + ChatColor.WHITE + name)
                .setLore(this.getDescription())
                .addAllItemFlags()
                .build();
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
