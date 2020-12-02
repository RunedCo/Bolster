package co.runed.bolster.commands;

import co.runed.bolster.gui.GuiMilestones;
import co.runed.bolster.items.Item;
import co.runed.bolster.items.LevelableItem;
import co.runed.bolster.managers.ItemManager;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.inventory.EquipmentSlot;

public class CommandLevelItem extends CommandBase
{
    public CommandLevelItem()
    {
        super("levelitem");
    }

    @Override
    public CommandAPICommand build()
    {
        return new CommandAPICommand(this.command)
                .withPermission("bolster.commands.levelitem")
                .executesPlayer(((sender, args) -> {

                    Item item = ItemManager.getInstance().getEquippedItem(sender, EquipmentSlot.HAND);

                    if (item instanceof LevelableItem)
                    {
                        LevelableItem levelableItem = (LevelableItem) item;

                        new GuiMilestones(null, levelableItem).show(sender);
                    }
                    else
                    {
                        sender.sendMessage("You are not holding an item that can be leveled.");
                    }
                }));
    }
}
