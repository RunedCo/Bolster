package co.runed.bolster.commands;

import co.runed.bolster.BolsterEntity;
import co.runed.bolster.classes.BolsterClass;
import co.runed.bolster.util.registries.Registries;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;
import org.ipvp.canvas.paginate.PaginatedMenuBuilder;
import org.ipvp.canvas.slot.SlotSettings;
import org.ipvp.canvas.template.StaticItemTemplate;
import org.ipvp.canvas.type.ChestMenu;

import java.util.ArrayList;
import java.util.List;

public class CommandBecomeGUI extends CommandBase
{
    public CommandBecomeGUI()
    {
        super("become");
    }

    @Override
    public CommandAPICommand build()
    {
        return new CommandAPICommand(this.command)
                .withPermission("bolster.commands.become")
                .executesPlayer(((sender, args) -> {
                    this.openMenu(sender);
                }));
    }

    private void openMenu(Player player)
    {
        List<BolsterClass> classes = new ArrayList<>();

        for (String id : Registries.CLASSES.getEntries().keySet())
        {
            classes.add(Registries.CLASSES.get(id));
        }

        ChestMenu.Builder pageTemplate = ChestMenu.builder(6).title("Classes").redraw(true);
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

        for (BolsterClass clazz : classes)
        {
            if (clazz == null) continue;

            SlotSettings settings = SlotSettings.builder()
                    .itemTemplate(new StaticItemTemplate(clazz.getIcon()))
                    .clickHandler((p, i) -> {
                        p.performCommand("become " + clazz.getId());
                        p.closeInventory();
                    })
                    .build();

            builder.addItem(settings);
        }

        List<Menu> pages = builder.build();

        pages.get(0).open(player);
    }
}