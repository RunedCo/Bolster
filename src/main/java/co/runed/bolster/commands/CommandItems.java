package co.runed.bolster.commands;

import co.runed.bolster.items.Item;
import co.runed.bolster.managers.ItemManager;
import co.runed.bolster.util.registries.Registries;
import co.runed.bolster.util.registries.Registry;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
                );
    }
}
