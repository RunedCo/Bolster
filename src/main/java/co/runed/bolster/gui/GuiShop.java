package co.runed.bolster.gui;

import co.runed.bolster.fx.Glyphs;
import co.runed.bolster.game.shop.Shop;
import co.runed.bolster.game.shop.ShopItem;
import co.runed.bolster.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;
import org.ipvp.canvas.paginate.PaginatedMenuBuilder;
import org.ipvp.canvas.slot.SlotSettings;
import org.ipvp.canvas.template.StaticItemTemplate;
import org.ipvp.canvas.type.ChestMenu;

import java.util.Collection;
import java.util.List;

public class GuiShop extends Gui
{
    Shop shop;

    public GuiShop(Gui previousGui, Shop shop)
    {
        super(previousGui);

        this.shop = shop;
    }

    @Override
    public String getTitle(Player player)
    {
        return shop.getName();
    }

    @Override
    protected Menu draw(Player player)
    {
        Collection<ShopItem> shopItems = this.shop.getItems().values();

        ChestMenu.Builder pageTemplate = ChestMenu.builder(6)
                .title(this.getTitle(player))
                .redraw(true);

        Mask itemSlots = BinaryMask.builder(pageTemplate.getDimensions())
                .pattern("111111111")
                .pattern("111111111")
                .pattern("111111111")
                .pattern("111111111")
                .pattern("111111111")
                .pattern("000000000")
                .build();

        PaginatedMenuBuilder builder = PaginatedMenuBuilder.builder(pageTemplate)
                .slots(itemSlots)
                .nextButton(GuiConstants.GUI_ARROW_RIGHT)
                .nextButtonSlot(51)
                .previousButton(GuiConstants.GUI_ARROW_LEFT)
                .previousButtonSlot(47);

        for (ShopItem item : shopItems)
        {
            ItemBuilder itemBuilder = new ItemBuilder(item.getIcon())
                    .addLore("")
                    .addLore(item.getLeftClickTooltip(player))
                    .addLore(item.getRightClickTooltip(player));

            if (item.canSell())
            {
                itemBuilder = itemBuilder.addLore(GuiConstants.RIGHT_CLICK_TO + "sell");
            }

            SlotSettings settings = SlotSettings.builder()
                    .itemTemplate(new StaticItemTemplate(itemBuilder.build()))
                    .clickHandler((p, info) -> {
                        if (info.getAction() == InventoryAction.PICKUP_ALL)
                        {
                            item.onLeftClick(this, p);
                        }
                        else if (info.getAction() == InventoryAction.PICKUP_HALF)
                        {
                            item.onRightClick(this, p);
                        }
                    })
                    .build();

            builder.addItem(settings);
        }

        List<Menu> pages = builder.build();

        return pages.get(0);
    }
}
