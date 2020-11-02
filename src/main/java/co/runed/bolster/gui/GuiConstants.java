package co.runed.bolster.gui;

import co.runed.bolster.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class GuiConstants
{
    public static final ItemStack GUI_DIVIDER = new ItemBuilder(Material.ITEM_FRAME).addAllItemFlags().hideName().setCustomModelData(1).build();
    public static final ItemStack GUI_ARROW_LEFT = new ItemBuilder(Material.ITEM_FRAME).addAllItemFlags().setDisplayName(ChatColor.BLUE + "" + ChatColor.BOLD + "Previous").setCustomModelData(2).build();
    public static final ItemStack GUI_ARROW_RIGHT = new ItemBuilder(Material.ITEM_FRAME).addAllItemFlags().setDisplayName(ChatColor.BLUE + "" + ChatColor.BOLD + "Previous").setCustomModelData(3).build();
    public static final ItemStack GUI_SAVE = new ItemBuilder(Material.ITEM_FRAME).addAllItemFlags().hideName().setCustomModelData(4).build();
    public static final ItemStack GUI_RELOAD = new ItemBuilder(Material.ITEM_FRAME).addAllItemFlags().hideName().setCustomModelData(5).build();
    public static final ItemStack GUI_CHECK = new ItemBuilder(Material.ITEM_FRAME).addAllItemFlags().setDisplayName(ChatColor.BLUE + "" + ChatColor.BOLD + "Confirm").setCustomModelData(6).build();
    public static final ItemStack GUI_CROSS = new ItemBuilder(Material.ITEM_FRAME).addAllItemFlags().setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Cancel").setCustomModelData(7).build();
    public static final ItemStack GUI_LOCK = new ItemBuilder(Material.ITEM_FRAME).addAllItemFlags().hideName().setCustomModelData(8).build();
    public static final ItemStack GUI_PLUS = new ItemBuilder(Material.ITEM_FRAME).addAllItemFlags().hideName().setCustomModelData(9).build();
}
