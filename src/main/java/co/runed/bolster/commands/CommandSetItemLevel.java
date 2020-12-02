package co.runed.bolster.commands;

import co.runed.bolster.items.Item;
import co.runed.bolster.items.LevelableItem;
import co.runed.bolster.managers.ItemManager;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

public class CommandSetItemLevel extends CommandBase
{
    public CommandSetItemLevel()
    {
        super("setitemlevel");
    }

    @Override
    public CommandAPICommand build()
    {
        return new CommandAPICommand(this.command)
                .withPermission("bolster.commands.setitemlevel")
                .withArguments(
                        new PlayerArgument("player"),
                        new IntegerArgument("level")
                )
                .executes((sender, args) -> {
                    Player player = (Player) args[0];
                    int level = (int) args[1];

                    Item item = ItemManager.getInstance().getEquippedItem(player, EquipmentSlot.HAND);

                    if (item instanceof LevelableItem)
                    {
                        ((LevelableItem) item).setLevel(level);
                        item.rebuild();

                        sender.sendMessage("Set " + item.getName() + "'s level to " + ((LevelableItem) item).getLevel() + " for player " + player.getDisplayName());
                    }
                    else
                    {
                        sender.sendMessage("Player " + player.getDisplayName() + " is not holding an item that can be leveled.");
                    }
                });
    }
}
