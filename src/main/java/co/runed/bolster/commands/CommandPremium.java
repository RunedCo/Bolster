package co.runed.bolster.commands;

import co.runed.bolster.game.PlayerData;
import co.runed.bolster.managers.PlayerManager;
import co.runed.bolster.util.TimeUtil;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Clock;
import java.time.Duration;
import java.time.Period;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAmount;
import java.util.Arrays;
import java.util.List;

public class CommandPremium extends CommandBase
{
    List<String> units = Arrays.asList("hours", "days", "months", "years");

    public CommandPremium()
    {
        super("premium");
    }

    private String[] getSuggestions(CommandSender sender)
    {
        return units.toArray(new String[0]);
    }

    private TemporalAmount fromString(long amount, String unit)
    {
        switch (unit)
        {
            case "hours":
            {
                return Duration.ofHours(amount);
            }
            case "days":
            {
                return Duration.ofDays(amount);
            }
            case "months":
            {
                return Period.ofMonths((int) amount);
            }
            case "years":
            {
                return Period.ofYears((int) amount);
            }
        }

        return Duration.ZERO;
    }

    @Override
    public CommandAPICommand build()
    {
        return new CommandAPICommand(this.command)
                .withPermission("bolster.commands.premium")
                .withSubcommand(new CommandAPICommand("add")
                        .withArguments(
                                new PlayerArgument("player"),
                                new IntegerArgument("time", -999999999, 999999999),
                                new StringArgument("unit").overrideSuggestions(this::getSuggestions)
                        )
                        .executes((sender, args) -> {
                            Player player = (Player) args[0];
                            int amount = (int) args[1];
                            String unit = (String) args[2];

                            if (!units.contains(unit))
                            {
                                sender.sendMessage("Invalid unit '" + unit + "'");
                                return;
                            }

                            PlayerManager.getInstance().getPlayerData(player).addPremiumExpiryTime(this.fromString(amount, unit));

                            sender.sendMessage("Added " + amount + " " + unit + " to " + player.getDisplayName());
                        })
                ).withSubcommand(new CommandAPICommand("remove")
                        .withArguments(
                                new PlayerArgument("player"),
                                new IntegerArgument("time"),
                                new StringArgument("unit").overrideSuggestions(this::getSuggestions)
                        )
                        .executes((sender, args) -> {
                            Player player = (Player) args[0];
                            int amount = (int) args[1];
                            String unit = (String) args[2];

                            if (!units.contains(unit))
                            {
                                sender.sendMessage("Invalid unit '" + unit + "'");
                                return;
                            }

                            PlayerManager.getInstance().getPlayerData(player).addPremiumExpiryTime(this.fromString(-amount, unit));

                            sender.sendMessage("Removed " + amount + " " + unit + " from " + player.getDisplayName());
                        })
                ).withSubcommand(new CommandAPICommand("set")
                        .withArguments(
                                new PlayerArgument("player"),
                                new IntegerArgument("time"),
                                new StringArgument("unit").overrideSuggestions(this::getSuggestions)
                        )
                        .executes((sender, args) -> {
                            Player player = (Player) args[0];
                            int amount = (int) args[1];
                            String unit = (String) args[2];

                            if (!units.contains(unit))
                            {
                                sender.sendMessage("Invalid unit '" + unit + "'");
                                return;
                            }

                            PlayerManager.getInstance().getPlayerData(player).setPremiumExpiryTime(ZonedDateTime.now(Clock.systemUTC()).plus(this.fromString(amount, unit)));

                            sender.sendMessage("Set premium time to " + amount + " " + unit + " for " + player.getDisplayName());
                        })
                )
                .withSubcommand(new CommandAPICommand("get")
                        .withArguments(
                                new PlayerArgument("player")
                        )
                        .executes((sender, args) -> {
                            Player player = (Player) args[0];

                            PlayerData playerData = PlayerManager.getInstance().getPlayerData(player);
                            ZonedDateTime expiryTime = playerData.getPremiumExpiryTime();

                            if (!playerData.isPremium())
                            {
                                sender.sendMessage(player.getName() + " is not a premium member!");
                                return;
                            }

                            String formattedDate = TimeUtil.formatDate(expiryTime);
                            String formattedHours = TimeUtil.formatDateRemainingPretty(expiryTime);

                            sender.sendMessage(player.getName() + "'s premium time will expire in " + ChatColor.YELLOW + formattedHours + ChatColor.WHITE + " (" + ChatColor.GREEN + formattedDate + ChatColor.WHITE + ")");
                        })
                );
    }
}
