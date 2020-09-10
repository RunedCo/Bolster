package co.runed.bolster.commands;

import co.runed.bolster.Bolster;
import co.runed.bolster.items.Item;
import co.runed.bolster.managers.ItemManager;
import co.runed.bolster.util.Category;
import co.runed.bolster.util.PlayerUtil;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
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

import java.util.*;
import java.util.concurrent.Callable;

public class CommandItems extends CommandBase
{
    public CommandItems()
    {
        super("items", "bolster.admin");
    }

    @Override
    public void run(CommandSender sender, Object[] args)
    {
        if (sender instanceof Player)
        {
            this.openMenu((Player) sender);
        }
    }

    private void openMenu(Player player)
    {
        HashMap<Category, List<ItemStack>> itemCategories = new HashMap<>();

        ChestMenu.Builder pageTemplate = ChestMenu.builder(6).title("Items").redraw(true);
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
                .nextButton(new ItemStack(Material.ARROW))
                .nextButtonSlot(51)
                .previousButton(new ItemStack(Material.ARROW))
                .previousButtonSlot(47);

        Map<String, Callable<? extends Item>> items = Bolster.getItemRegistry().getEntries();

        for (String id : items.keySet())
        {
            Item item = Bolster.getItemRegistry().createInstance(id);

            for (Category category : item.getCategories())
            {
                itemCategories.putIfAbsent(category, new ArrayList<>());

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
                        this.openCategory(player, category, categoryItems);
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

        pages.get(0).open(player);
    }

    private void openCategory(Player player, Category category, List<ItemStack> items)
    {
        ChestMenu.Builder pageTemplate = ChestMenu.builder(6).title("Items - " + category.getName()).redraw(true);
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
                .nextButton(new ItemStack(Material.ARROW))
                .nextButtonSlot(51)
                .previousButton(new ItemStack(Material.ARROW))
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

        pages.get(0).open(player);
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
