package co.runed.bolster.gui;

import co.runed.bolster.items.Item;
import co.runed.bolster.managers.ItemManager;
import co.runed.bolster.util.Category;
import co.runed.bolster.util.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.ClickInformation;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;
import org.ipvp.canvas.paginate.PaginatedMenuBuilder;
import org.ipvp.canvas.slot.SlotSettings;
import org.ipvp.canvas.template.StaticItemTemplate;
import org.ipvp.canvas.type.ChestMenu;

import java.util.List;

public class GuiCategory extends Gui
{
    Category category;
    List<ItemStack> items;

    public GuiCategory(Gui previousGui, Category category, List<ItemStack> items)
    {
        super(previousGui);

        this.previousGui = previousGui;
        this.category = category;
        this.items = items;
    }

    @Override
    public String getTitle(Player player)
    {
        String title = this.category.getName();

        if (this.previousGui != null)
        {
            title = this.previousGui.getTitle(player) + " - " + title;
        }

        return title;
    }

    @Override
    public Menu draw(Player player)
    {
        ChestMenu.Builder pageTemplate = ChestMenu.builder(6).title("Items - " + category.getName());
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

        for (ItemStack item : items)
        {
            SlotSettings settings = SlotSettings.builder()
                    .itemTemplate(new StaticItemTemplate(item))
                    .clickHandler(this::givePlayerItem)
                    .build();

            builder.addItem(settings);
        }

        List<Menu> pages = builder.build();

        return pages.get(0);
    }

    private void givePlayerItem(Player player, ClickInformation info)
    {
        int stackAmount = info.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY || info.getAction() == InventoryAction.DROP_ALL_SLOT ? 64 : 1;
        ItemStack stack = info.getClickedSlot().getItem(player);
        String itemId = ItemManager.getInstance().getItemIdFromStack(stack);

        if (info.getAction() == InventoryAction.DROP_ALL_SLOT || info.getAction() == InventoryAction.DROP_ONE_SLOT)
        {
            Item item = ItemManager.getInstance().createItem(player, itemId);

            ItemStack itemStack = item.toItemStack();
            itemStack.setAmount(stackAmount);

            PlayerUtil.dropItem(player, itemStack);
            return;
        }

        ItemManager.getInstance().giveItem(player, itemId, stackAmount);
    }
}
