package co.runed.bolster.items;

import co.runed.bolster.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemCategory
{
    public static final ItemCategory WEAPONS = new ItemCategory("Weapons", null, new ItemStack(Material.IRON_SWORD));
    public static final ItemCategory TOOLS = new ItemCategory("Tools", null, new ItemStack(Material.IRON_AXE));
    public static final ItemCategory MATERIALS = new ItemCategory("Materials", null, new ItemStack(Material.COBBLESTONE));
    public static final ItemCategory ALL = new ItemCategory("All Items", null, new ItemStack(Material.CHEST));

    String name;
    String description;
    ItemStack icon;

    public ItemCategory(String name, String description, ItemStack icon)
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
