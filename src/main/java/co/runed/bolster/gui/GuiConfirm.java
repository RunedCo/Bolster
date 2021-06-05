package co.runed.bolster.gui;

import co.runed.bolster.util.ItemBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.slot.SlotSettings;
import org.ipvp.canvas.template.ItemStackTemplate;
import org.ipvp.canvas.template.StaticItemTemplate;
import org.ipvp.canvas.type.ChestMenu;

public class GuiConfirm extends Gui
{
    ItemStack icon;
    Runnable onConfirm;

    public GuiConfirm(Gui previousGui, ItemStack icon, Runnable onConfirm)
    {
        super(previousGui);

        this.icon = icon;
        this.onConfirm = onConfirm;
    }

    @Override
    public String getTitle(Player player)
    {
        return "Confirm";
    }

    @Override
    public Menu draw(Player player)
    {
        ChestMenu.Builder pageTemplate = ChestMenu.builder(1)
                .title(this.getTitle(player))
                .redraw(true);

        Menu menu = pageTemplate.build();

        ItemStackTemplate declineTemplate = new StaticItemTemplate(
                new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                        .setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "CANCEL")
                        .build()
        );

        for (int i = 0; i < 4; i++)
        {
            SlotSettings settings = SlotSettings.builder()
                    .itemTemplate(declineTemplate)
                    .clickHandler((p, info) -> {
                        menu.close(p);
                        if (this.previousGui != null) this.previousGui.show(p);
                    })
                    .build();

            menu.getSlot(i).setSettings(settings);
        }

        ItemStackTemplate confirmTemplate = new StaticItemTemplate(
                new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                        .setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "CONFIRM")
                        .build()
        );

        for (int i = 5; i < 9; i++)
        {
            SlotSettings settings = SlotSettings.builder()
                    .itemTemplate(confirmTemplate)
                    .clickHandler((p, info) -> {
                        if (info.getAction() == InventoryAction.PICKUP_ALL)
                        {
                            this.onConfirm.run();
                            menu.close(p);
                            if (this.previousGui != null) this.previousGui.show(p);
                        }
                    })
                    .build();

            menu.getSlot(i).setSettings(settings);
        }

        menu.getSlot(4).setSettings(
                SlotSettings.builder()
                        .itemTemplate(new StaticItemTemplate(this.icon))
                        .build());

        return menu;
    }
}
