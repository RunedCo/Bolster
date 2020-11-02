package co.runed.bolster.commands;

import co.runed.bolster.Bolster;
import co.runed.bolster.gui.GuiItems;
import co.runed.bolster.items.Item;
import co.runed.bolster.managers.ItemManager;
import co.runed.bolster.util.Category;
import co.runed.bolster.util.PlayerUtil;
import co.runed.bolster.util.registries.Registry;
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
            new GuiItems().show((Player)sender);
        }
    }
}
