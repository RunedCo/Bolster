package co.runed.bolster.gui;

import co.runed.bolster.fx.Glyphs;
import co.runed.bolster.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class GuiConstants
{
    public static final ItemStack GUI_DIVIDER = new ItemBuilder(Material.ITEM_FRAME).addAllItemFlags().hideName().setCustomModelData(1).build();
    public static final ItemStack GUI_ARROW_LEFT = new ItemBuilder(Material.ITEM_FRAME).addAllItemFlags().setDisplayName(ChatColor.BLUE + "" + ChatColor.BOLD + "Previous").setCustomModelData(2).build();
    public static final ItemStack GUI_ARROW_RIGHT = new ItemBuilder(Material.ITEM_FRAME).addAllItemFlags().setDisplayName(ChatColor.BLUE + "" + ChatColor.BOLD + "Next").setCustomModelData(3).build();
    public static final ItemStack GUI_SAVE = new ItemBuilder(Material.ITEM_FRAME).addAllItemFlags().hideName().setCustomModelData(4).build();
    public static final ItemStack GUI_RELOAD = new ItemBuilder(Material.ITEM_FRAME).addAllItemFlags().hideName().setCustomModelData(5).build();
    public static final ItemStack GUI_CHECK = new ItemBuilder(Material.ITEM_FRAME).addAllItemFlags().setDisplayName(ChatColor.BLUE + "" + ChatColor.BOLD + "Confirm").setCustomModelData(6).build();
    public static final ItemStack GUI_CROSS = new ItemBuilder(Material.ITEM_FRAME).addAllItemFlags().setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Cancel").setCustomModelData(7).build();
    public static final ItemStack GUI_LOCK = new ItemBuilder(Material.ITEM_FRAME).addAllItemFlags().hideName().setCustomModelData(8).build();
    public static final ItemStack GUI_PLUS = new ItemBuilder(Material.ITEM_FRAME).addAllItemFlags().hideName().setCustomModelData(9).build();

    public static final String CLICK_TO = ChatColor.GREEN + "" + Glyphs.ARROW + " Click to ";
    public static final String CLICK_TO_SELECT = CLICK_TO + "select";

    public static final String RIGHT_CLICK_TO = ChatColor.GREEN + "" + Glyphs.ARROW + " Right-click to ";

    public static final String CANNOT_AFFORD_TO = ChatColor.RED + "You cannot afford ";

    public static final String LOCKED = ChatColor.RED + "" + ChatColor.BOLD + "LOCKED";
    public static final String UNLOCKED = ChatColor.GREEN + "" + ChatColor.BOLD + "UNLOCKED";
    public static final String SELECTED = ChatColor.GREEN + "" + ChatColor.BOLD + "SELECTED";
    public static final String OWNED = ChatColor.GREEN + "" + ChatColor.BOLD + "OWNED";
}
