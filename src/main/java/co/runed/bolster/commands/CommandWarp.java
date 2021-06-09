package co.runed.bolster.commands;

import co.runed.bolster.Warps;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.command.CommandSender;

public class CommandWarp extends CommandBase
{
    public CommandWarp()
    {
        super("warp");
    }

    private String[] getSuggestions(CommandSender sender)
    {
        return Warps.getInstance().getWarps().values().stream().map(w -> w.id).toArray(String[]::new);
    }

    @Override
    public CommandAPICommand build()
    {
        return new CommandAPICommand(this.command)
                .withPermission("bolster.commands.warp")
                .withArguments(new StringArgument("warp").overrideSuggestions(this::getSuggestions))
                .executesPlayer(((sender, args) -> {
                    String warpId = (String) args[0];

                    if (!Warps.getInstance().hasWarp(warpId))
                    {
                        sender.sendMessage("Invalid warp!");
                        return;
                    }

                    Warps.Warp warp = Warps.getInstance().getWarp(warpId);

                    warp.teleport(sender);
                    sender.sendMessage("Warping to " + (warp.name == null ? warp.id : warp.name));
                }))
                .withSubcommand(new CommandAPICommand("set")
                        .withArguments(
                                new StringArgument("name")
                        )
                        .executesPlayer((sender, args) -> {
                            String id = (String) args[0];

                            Warps.getInstance().addWarp(id, sender.getLocation());

                            sender.sendMessage("Created warp " + id);
                        })
                )
                .withSubcommand(new CommandAPICommand("remove")
                        .withArguments(
                                new StringArgument("warp").overrideSuggestions(this::getSuggestions)
                        )
                        .executesPlayer((sender, args) -> {
                            String warpId = (String) args[0];

                            if (!Warps.getInstance().hasWarp(warpId))
                            {
                                sender.sendMessage("Invalid warp!");
                                return;
                            }

                            Warps.getInstance().removeWarp(warpId);

                            sender.sendMessage("Removed warp " + warpId);
                        })
                );
    }
}