package co.runed.bolster.commands;

import co.runed.bolster.game.PlayerData;
import co.runed.bolster.game.currency.Currency;
import co.runed.bolster.managers.PlayerManager;
import co.runed.bolster.util.TimeUtil;
import co.runed.bolster.util.registries.Registries;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.Period;
import java.time.YearMonth;
import java.util.Date;

public class CommandPremium extends CommandBase
{
    public CommandPremium()
    {
        super("premium");
    }

    private String[] getSuggestions(CommandSender sender)
    {
        return new String[]{"hours", "days", "months", "years"};
    }

    private Duration fromString(long amount, String unit)
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
                return Duration.ofDays(Period.ofMonths((int) amount).getDays());
            }
            case "years":
            {
                return Duration.ofDays(Period.ofYears((int) amount).getDays());
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
                                new IntegerArgument("time"),
                                new StringArgument("unit").overrideSuggestions(this::getSuggestions)
                        )
                        .executes((sender, args) -> {
                            Player player = (Player) args[0];
                            int amount = (int) args[1];
                            String unit = (String) args[2];

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

                            PlayerManager.getInstance().getPlayerData(player).addPremiumExpiryTime(this.fromString(amount, unit).negated());

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

                            PlayerManager.getInstance().getPlayerData(player).setPremiumExpiryTime(Instant.now().plus(this.fromString(amount, unit)));

                            sender.sendMessage("Set premium time to " + amount + unit + " for " + player.getDisplayName());
                        })
                )
                .withSubcommand(new CommandAPICommand("get")
                        .withArguments(
                                new PlayerArgument("player")
                        )
                        .executes((sender, args) -> {
                            Player player = (Player) args[0];

                            PlayerData playerData = PlayerManager.getInstance().getPlayerData(player);
                            Instant expiryTime = playerData.getPremiumExpiryTime();

                            if (!playerData.isPremium())
                            {
                                sender.sendMessage(player.getName() + " is not a premium member!");
                                return;
                            }

                            String formattedDate = TimeUtil.formatInstantAsDate(expiryTime);
                            String formattedHours = TimeUtil.formatInstantAsPrettyTimeLeft(expiryTime);

                            sender.sendMessage(player.getName() + "'s premium time will expire in " + ChatColor.YELLOW + formattedHours + ChatColor.WHITE + " (" + ChatColor.GREEN + formattedDate + ChatColor.WHITE + ")");
                        })
                );
    }
}
