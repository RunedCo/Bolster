package co.runed.bolster.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;
import org.ipvp.canvas.paginate.PaginatedMenuBuilder;
import org.ipvp.canvas.type.ChestMenu;

public class GuiShopConfirm extends Gui
{
    @Override
    public String getTitle(Player player)
    {
        return "Confirm Purchase";
    }

    @Override
    public Menu draw(Player player)
    {
        ChestMenu.Builder pageTemplate = ChestMenu.builder(1)
                .title(this.getTitle(player))
                .redraw(true);

        Mask confirmMask = BinaryMask.builder(pageTemplate.getDimensions())
                .pattern("000001111")
                .item(new ItemStack(Material.LIME_STAINED_GLASS_PANE))
                .build();

        Mask declineMask = BinaryMask.builder(pageTemplate.getDimensions())
                .pattern("111100000")
                .item(new ItemStack(Material.RED_STAINED_GLASS_PANE))
                .build();

        Menu menu = pageTemplate.build();

        confirmMask.apply(menu);
        declineMask.apply(menu);

        return menu;
    }
}
