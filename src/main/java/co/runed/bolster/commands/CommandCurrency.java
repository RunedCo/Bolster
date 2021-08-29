package co.runed.bolster.commands;

import co.runed.bolster.Permissions;
import co.runed.bolster.managers.PlayerManager;
import co.runed.bolster.util.registries.Registries;
import co.runed.bolster.util.registries.Registry;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandCurrency extends CommandBase {
    public CommandCurrency() {
        super("currency");
    }

    private String[] getSuggestions(CommandSender sender) {
        return Registries.CURRENCIES.getEntries().values().stream().map(Registry.Entry::getId).toArray(String[]::new);
    }

    @Override
    public CommandAPICommand build() {
        return new CommandAPICommand(this.command)
                .withPermission(Permissions.COMMAND_CURRENCY)
                .withSubcommand(new CommandAPICommand("add")
                        .withArguments(
                                new PlayerArgument("player"),
                                new StringArgument("currency").overrideSuggestions(this::getSuggestions),
                                new IntegerArgument("amount")
                        )
                        .executes((sender, args) -> {
                            var player = (Player) args[0];
                            var id = (String) args[1];
                            var amount = (int) args[2];

                            if (!Registries.CURRENCIES.contains(id)) {
                                sender.sendMessage("Invalid currency id '" + id + "'");
                                return;
                            }

                            var currency = Registries.CURRENCIES.get(id);
                            PlayerManager.getInstance().getPlayerData(player).addCurrency(currency, amount);

                            sender.sendMessage("Added " + amount + " " + currency.getPluralisedName() + " to " + player.getDisplayName());
                        })
                ).withSubcommand(new CommandAPICommand("remove")
                        .withArguments(
                                new PlayerArgument("player"),
                                new StringArgument("currency").overrideSuggestions(this::getSuggestions),
                                new IntegerArgument("amount")
                        )
                        .executes((sender, args) -> {
                            var player = (Player) args[0];
                            var id = (String) args[1];
                            var amount = (int) args[2];

                            if (!Registries.CURRENCIES.contains(id)) {
                                sender.sendMessage("Invalid currency id '" + id + "'");
                                return;
                            }

                            var currency = Registries.CURRENCIES.get(id);
                            PlayerManager.getInstance().getPlayerData(player).addCurrency(currency, -amount);

                            sender.sendMessage("Removed " + amount + " " + currency.getPluralisedName() + " from " + player.getDisplayName());
                        })
                ).withSubcommand(new CommandAPICommand("set")
                        .withArguments(
                                new PlayerArgument("player"),
                                new StringArgument("currency").overrideSuggestions(this::getSuggestions),
                                new IntegerArgument("amount")
                        )
                        .executes((sender, args) -> {
                            var player = (Player) args[0];
                            var id = (String) args[1];
                            var amount = (int) args[2];

                            if (!Registries.CURRENCIES.contains(id)) {
                                sender.sendMessage("Invalid currency id '" + id + "'");
                                return;
                            }

                            var currency = Registries.CURRENCIES.get(id);
                            PlayerManager.getInstance().getPlayerData(player).setCurrency(currency, amount);

                            sender.sendMessage("Set " + currency.getPluralisedName() + " to " + amount + " for " + player.getDisplayName());
                        })
                )
                .withSubcommand(new CommandAPICommand("get")
                        .withArguments(
                                new PlayerArgument("player"),
                                new StringArgument("currency").overrideSuggestions(this::getSuggestions)
                        )
                        .executes((sender, args) -> {
                            var player = (Player) args[0];
                            var id = (String) args[1];

                            if (!Registries.CURRENCIES.contains(id)) {
                                sender.sendMessage("Invalid currency id '" + id + "'");
                                return;
                            }

                            var currency = Registries.CURRENCIES.get(id);
                            var amount = PlayerManager.getInstance().getPlayerData(player).getCurrency(currency);
                            sender.sendMessage(player.getName() + " has " + amount + " " + currency.getPluralisedName());
                        })
                );
    }
}
