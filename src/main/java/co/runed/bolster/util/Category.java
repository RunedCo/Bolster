package co.runed.bolster.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Category {
    public static final Category WEAPONS = new Category("Weapons", null, new ItemStack(Material.IRON_SWORD));
    public static final Category TOOLS = new Category("Tools", null, new ItemStack(Material.IRON_AXE));
    public static final Category MATERIALS = new Category("Materials", null, new ItemStack(Material.COBBLESTONE));
    public static final Category UTILITY = new Category("Utility", null, new ItemStack(Material.FURNACE));
    public static final Category HATS = new Category("Hats", null, new ItemStack(Material.PLAYER_HEAD));
    public static final Category LEVELABLE = new Category("Levelable", null, new ItemStack(Material.EXPERIENCE_BOTTLE));
    public static final Category RANGED = new Category("Ranged", null, new ItemStack(Material.BOW));
    public static final Category ALL = new Category("All", null, new ItemStack(Material.CHEST));

    String name;
    String description;
    ItemStack icon;

    public Category(String name, String description, ItemStack icon) {
        this.name = name;
        this.description = description;
        this.icon = new ItemBuilder(icon)
                .setDisplayName(Component.text(name, NamedTextColor.WHITE))
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
