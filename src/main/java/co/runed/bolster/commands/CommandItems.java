package co.runed.bolster.commands;

import co.runed.bolster.items.Item;
import co.runed.bolster.items.LevelableItem;
import co.runed.bolster.managers.ItemManager;
import co.runed.bolster.managers.PlayerManager;
import co.runed.bolster.util.Category;
import co.runed.bolster.util.Definition;
import co.runed.bolster.util.registries.Registries;
import co.runed.bolster.util.registries.Registry;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Command("items")
public class CommandItems extends CommandBase
{
    public CommandItems()
    {
        super("items");
    }

    private String[] getSuggestions(CommandSender sender)
    {
        return Registries.ITEMS.getEntries().values().stream().map(Registry.Entry::getId).toArray(String[]::new);
    }

    private String[] getLevelableSuggections(CommandSender sender)
    {
        List<String> items = new ArrayList<>();

        for (Registry.Entry<? extends Definition<Item>> item : Registries.ITEMS.getEntries().values())
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
                .withPermission("bolster.commands.items")
                .withSubcommand(new CommandAPICommand("give")
                        .withArguments(
                                new PlayerArgument("player"),
                                new StringArgument("item_id").overrideSuggestions(this::getSuggestions),
                                new IntegerArgument("amount", 0)
                        )
                        .executes((sender, args) -> {
                            Player player = (Player) args[0];
                            String id = (String) args[1];
                            int amount = (int) args[2];

                            Item item = ItemManager.getInstance().giveItem(player, player.getInventory(), id, amount);

                            sender.sendMessage("Gave " + amount + " " + item.getName() + " to " + player.getDisplayName());
                        })
                )
                .withSubcommand(new CommandAPICommand("remove")
                        .withArguments(
                                new PlayerArgument("player"),
                                new StringArgument("item_id").overrideSuggestions(this::getSuggestions),
                                new IntegerArgument("amount", 0)
                        )
                        .executes((sender, args) -> {
                            Player player = (Player) args[0];
                            String id = (String) args[1];
                            int amount = (int) args[2];

                            int amountInInv = ItemManager.getInstance().getItemCount(player.getInventory(), id);

                            Item item = ItemManager.getInstance().createItem(player, id);

                            ItemManager.getInstance().removeItem(player.getInventory(), item, amount);

                            sender.sendMessage("Removed " + Math.min(amountInInv, amount) + " " + item.getName() + " from " + player.getDisplayName());
                        })
                )
                .withSubcommand(new CommandAPICommand("clear")
                        .withArguments(
                                new PlayerArgument("player"),
                                new StringArgument("item_id").overrideSuggestions(this::getSuggestions)
                        )
                        .executes((sender, args) -> {
                            Player player = (Player) args[0];
                            String id = (String) args[1];

                            Item item = ItemManager.getInstance().createItem(player, id);

                            ItemManager.getInstance().removeItem(player.getInventory(), item, Integer.MAX_VALUE);

                            sender.sendMessage("Cleared all " + item.getName() + " from " + player.getDisplayName());
                        })
                )
                .withSubcommand(new CommandAPICommand("setlevel")
                        .withArguments(
                                new PlayerArgument("player"),
                                new StringArgument("item_id").overrideSuggestions(this::getLevelableSuggections),
                                new IntegerArgument("amount", 0)
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

                            Item item = Registries.ITEMS.get(id).create();

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
                        })
                );
    }
}
