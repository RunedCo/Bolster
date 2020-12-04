package co.runed.bolster.commands;

import co.runed.bolster.items.Item;
import co.runed.bolster.items.LevelableItem;
import co.runed.bolster.managers.ItemManager;
import co.runed.bolster.managers.PlayerManager;
import co.runed.bolster.util.Category;
import co.runed.bolster.util.registries.Registries;
import co.runed.bolster.util.registries.Registry;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandSetItemLevel extends CommandBase
{
    public CommandSetItemLevel()
    {
        super("setitemlevel");
    }

    private String[] getSuggestions(CommandSender sender)
    {
        List<String> items = new ArrayList<>();

        for (Registry.Entry<? extends Item> item : Registries.ITEMS.getEntries().values())
        {
            if (!item.getCategories().contains(Category.LEVELABLE)) continue;

            items.add(item.getId());
        }

        return items.toArray(new String[0]);
    }

    @Override
    public CommandAPICommand build()
    {
        return new CommandAPICommand(this.command)
                .withPermission("bolster.commands.setitemlevel")
                .withArguments(
                        new PlayerArgument("player"),
                        new StringArgument("item_id").overrideSuggestions(this::getSuggestions),
                        new IntegerArgument("level")
                )
                .executes((sender, args) -> {
                    Player player = (Player) args[0];
                    String id = (String) args[1];
                    int level = (int) args[2];

                    if (!Registries.ITEMS.contains(id))
                    {
                        sender.sendMessage("Invalid item id '" + id + "'");
                        return;
                    }

                    Item item = Registries.ITEMS.get(id);

                    if (item instanceof LevelableItem)
                    {
                        LevelableItem fetchedItem = (LevelableItem) ItemManager.getInstance().getItem(player, id);

                        PlayerManager.getInstance().getPlayerData(player).setItemLevel(id, level);

                        if (fetchedItem != null)
                        {
                            fetchedItem.setLevel(level);
                            fetchedItem.rebuild();
                        }

                        sender.sendMessage("Set " + item.getName() + "'s level to " + Math.min(level, ((LevelableItem) item).getMaxLevel()) + " for player " + player.getDisplayName());
                    }
                    else
                    {
                        sender.sendMessage(item.getName() + ChatColor.WHITE + " is not an item that can be leveled.");
                    }
                });
    }
}
