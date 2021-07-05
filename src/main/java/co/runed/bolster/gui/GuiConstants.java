package co.runed.bolster.gui;

import co.runed.bolster.fx.Glyphs;
import co.runed.bolster.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class GuiConstants
{
    public static final String CLICK_TO = ChatColor.GREEN + "" + Glyphs.ARROW + " Click to ";
    public static final String CLICK_TO_SELECT = CLICK_TO + "select";

    public static final String RIGHT_CLICK_TO = ChatColor.GREEN + "" + Glyphs.ARROW + " Right-click to ";

    public static final String CANNOT_AFFORD_TO = ChatColor.RED + "You cannot afford ";

    public static final String LOCKED = ChatColor.RED + "" + ChatColor.BOLD + "LOCKED";
    public static final String UNLOCKED = ChatColor.GREEN + "" + ChatColor.BOLD + "UNLOCKED";
    public static final String SELECTED = ChatColor.GREEN + "" + ChatColor.BOLD + "SELECTED";
    public static final String OWNED = ChatColor.GREEN + "" + ChatColor.BOLD + "OWNED";

    public static final String PREVIOUS = "Previous";
    public static final String NEXT = "Next";
    public static final String CONFIRM = "Confirm";
    public static final String CANCEL = "Cancel";

    public static final ItemStack GUI_DIVIDER = new ItemBuilder(Material.ITEM_FRAME).addAllItemFlags().hideName().setCustomModelData(1).build();
    public static final ItemStack GUI_ARROW_LEFT = new ItemBuilder(Material.ITEM_FRAME).addAllItemFlags().setDisplayName(Component.text(PREVIOUS, NamedTextColor.BLUE, TextDecoration.BOLD)).setCustomModelData(2).build();
    public static final ItemStack GUI_ARROW_RIGHT = new ItemBuilder(Material.ITEM_FRAME).addAllItemFlags().setDisplayName(Component.text(NEXT, NamedTextColor.BLUE, TextDecoration.BOLD)).setCustomModelData(3).build();
    public static final ItemStack GUI_SAVE = new ItemBuilder(Material.ITEM_FRAME).addAllItemFlags().hideName().setCustomModelData(4).build();
    public static final ItemStack GUI_RELOAD = new ItemBuilder(Material.ITEM_FRAME).addAllItemFlags().hideName().setCustomModelData(5).build();
    public static final ItemStack GUI_CHECK = new ItemBuilder(Material.ITEM_FRAME).addAllItemFlags().setDisplayName(Component.text(CONFIRM, NamedTextColor.BLUE, TextDecoration.BOLD)).setCustomModelData(6).build();
    public static final ItemStack GUI_CROSS = new ItemBuilder(Material.ITEM_FRAME).addAllItemFlags().setDisplayName(Component.text(CANCEL, NamedTextColor.RED, TextDecoration.BOLD)).setCustomModelData(7).build();
    public static final ItemStack GUI_LOCK = new ItemBuilder(Material.ITEM_FRAME).addAllItemFlags().hideName().setCustomModelData(8).build();
    public static final ItemStack GUI_PLUS = new ItemBuilder(Material.ITEM_FRAME).addAllItemFlags().hideName().setCustomModelData(9).build();
}
