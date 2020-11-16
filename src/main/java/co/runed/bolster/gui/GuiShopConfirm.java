package co.runed.bolster.gui;

import co.runed.bolster.shop.ShopItem;
import co.runed.bolster.util.ItemBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;
import org.ipvp.canvas.template.ItemStackTemplate;
import org.ipvp.canvas.template.StaticItemTemplate;
import org.ipvp.canvas.type.ChestMenu;

public class GuiShopConfirm extends Gui
{
    ShopItem shopItem;

    public GuiShopConfirm(Gui previousGui, ShopItem shopItem)
    {
        super(previousGui);

        this.shopItem = shopItem;
    }

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

        ItemStackTemplate confirmTemplate = new StaticItemTemplate(
                new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                        .setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "CONFIRM")
                        .build()
        );

        Mask confirmMask = BinaryMask.builder(pageTemplate.getDimensions())
                .pattern("000001111")
                .item(confirmTemplate)
                .build();

        ItemStackTemplate declineTemplate = new StaticItemTemplate(
                new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                        .setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "CANCEL")
                        .build()
        );

        Mask declineMask = BinaryMask.builder(pageTemplate.getDimensions())
                .pattern("111100000")
                .item(declineTemplate)
                .build();

        Menu menu = pageTemplate.build();

        confirmMask.apply(menu);
        declineMask.apply(menu);

        return menu;
    }
}
