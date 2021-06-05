package co.runed.bolster.gui;

import co.runed.bolster.items.Item;
import co.runed.bolster.util.Category;
import co.runed.bolster.util.registries.Registries;
import co.runed.bolster.util.registries.Registry;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;
import org.ipvp.canvas.paginate.PaginatedMenuBuilder;
import org.ipvp.canvas.slot.SlotSettings;
import org.ipvp.canvas.template.StaticItemTemplate;
import org.ipvp.canvas.type.ChestMenu;

import java.util.*;

public class GuiItems extends Gui
{
    public GuiItems(Gui previousGui)
    {
        super(previousGui);
    }

    @Override
    public String getTitle(Player player)
    {
        return "Items";
    }

    @Override
    public Menu draw(Player player)
    {
        HashMap<Category, List<ItemStack>> itemCategories = new HashMap<>();

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

        Map<String, Registry.Entry<? extends Item>> items = Registries.ITEMS.getEntries();

        for (Registry.Entry<? extends Item> entry : items.values())
        {
            Item item = entry.create();

            for (Category category : entry.getCategories())
            {
                itemCategories.putIfAbsent(category, new ArrayList<>());

                item.setEntity(player, false);
                item.setEntity(null, false);
                item.rebuild();

                itemCategories.get(category).add(item.toItemStack());
            }

            item.destroy();
        }

        List<Category> sortedCategories = new ArrayList<>(itemCategories.keySet());
        sortedCategories.sort(Comparator.comparing(Category::getName));

        SlotSettings allItems = null;

        for (Category category : sortedCategories)
        {
            List<ItemStack> categoryItems = itemCategories.get(category);

            SlotSettings settings = SlotSettings.builder()
                    .itemTemplate(new StaticItemTemplate(category.getIcon()))
                    .clickHandler((p, info) -> {
                        new GuiCategory(this, category, categoryItems).show(player);
                    })
                    .build();

            if (category == Category.ALL)
            {
                allItems = settings;

                continue;
            }

            builder.addItem(settings);
        }

        if (allItems != null) builder.addItem(allItems);

        List<Menu> pages = builder.build();

        return pages.get(0);
    }
}
