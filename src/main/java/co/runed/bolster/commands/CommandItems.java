package co.runed.bolster.commands;

import co.runed.bolster.Bolster;
import co.runed.bolster.items.Item;
import co.runed.bolster.managers.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ipvp.canvas.ClickInformation;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;
import org.ipvp.canvas.mask.Mask2D;
import org.ipvp.canvas.paginate.PaginatedMenuBuilder;
import org.ipvp.canvas.slot.SlotSettings;
import org.ipvp.canvas.template.ItemStackTemplate;
import org.ipvp.canvas.template.StaticItemTemplate;
import org.ipvp.canvas.type.ChestMenu;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CommandItems extends CommandBase
{
    public CommandItems()
    {
        super("items", "bolster.admin", null, null);
    }

    @Override
    public void run(CommandSender sender, Object[] args)
    {
        if(sender instanceof Player) {
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

            Map<String, Class<? extends Item>> items = Bolster.getItemRegistry().getEntries();

            for (String id : items.keySet()){
                Item item = Bolster.getItemRegistry().createInstance(id);

                SlotSettings settings = SlotSettings.builder()
                        .itemTemplate(new StaticItemTemplate(item.toItemStack()))
                        .clickHandler(this::onMenuClick)
                        .build();

                builder.addItem(settings);
            }

            List<Menu> pages = builder.build();

            pages.get(0).open((Player)sender);
        }
    }

    private void onMenuClick(Player player, ClickInformation info) {
        int stackAmount = info.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY ? 64 : 1;
        ItemStack stack = info.getClickedSlot().getItem(player);
        String itemId = Bolster.getItemManager().getItemIdFromStack(stack);

        if(itemId != null) {
            Item item = Bolster.getItemManager().getOrCreateItem(player, itemId);
            ItemStack itemStack = item.toItemStack();
            itemStack.setAmount(stackAmount);

            player.getInventory().addItem(itemStack);
        }

        player.sendMessage("You clicked on an item " + info.getAction().toString());
    }
}
