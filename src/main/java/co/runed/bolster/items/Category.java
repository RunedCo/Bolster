package co.runed.bolster.items;

import co.runed.bolster.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Category
{
    public static final Category WEAPONS = new Category("Weapons", null, new ItemStack(Material.IRON_SWORD));
    public static final Category TOOLS = new Category("Tools", null, new ItemStack(Material.IRON_AXE));
    public static final Category MATERIALS = new Category("Materials", null, new ItemStack(Material.COBBLESTONE));
    public static final Category UTILITY = new Category("Utility", null, new ItemStack(Material.FURNACE));
    public static final Category HAT = new Category("Hat", null, new ItemStack(Material.PLAYER_HEAD));
    public static final Category ALL = new Category("All Items", null, new ItemStack(Material.CHEST));

    String name;
    String description;
    ItemStack icon;

    public Category(String name, String description, ItemStack icon)
    {
        this.name = name;
        this.description = description;
        this.icon = new ItemBuilder(icon)
                .setDisplayName(ChatColor.WHITE + name)
                .setLore(this.getDescription())
                .addAllItemFlags()
                .build();
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public ItemStack getIcon()
    {


        return icon;
    }
}
