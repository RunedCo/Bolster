package co.runed.bolster.commands;

import co.runed.bolster.Permissions;
import co.runed.bolster.Warps;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.command.CommandSender;

public class CommandWarp extends CommandBase {
    public CommandWarp() {
        super("warp");
    }

    private String[] getSuggestions(CommandSender sender) {
        return Warps.getInstance().getWarps().values().stream().map(w -> w.id).toArray(String[]::new);
    }

    @Override
    public CommandAPICommand build() {
        return new CommandAPICommand(this.command)
                .withPermission(Permissions.COMMAND_WARP)
                .withArguments(new StringArgument("warp").overrideSuggestions(this::getSuggestions))
                .executesPlayer(((sender, args) -> {
                    var warpId = (String) args[0];

                    if (!Warps.getInstance().hasWarp(warpId)) {
                        sender.sendMessage("Invalid warp!");
                        return;
                    }

                    var warp = Warps.getInstance().getWarp(warpId);

                    warp.teleport(sender);
                    sender.sendMessage("Warping to " + (warp.name == null ? warp.id : warp.name));
                }))
                .withSubcommand(new CommandAPICommand("set")
                        .withArguments(
                                new StringArgument("name")
                        )
                        .executesPlayer((sender, args) -> {
                            var id = (String) args[0];

                            Warps.getInstance().addWarp(id, sender.getLocation());

                            sender.sendMessage("Created warp " + id);
                        })
                )
                .withSubcommand(new CommandAPICommand("remove")
                        .withArguments(
                                new StringArgument("warp").overrideSuggestions(this::getSuggestions)
                        )
                        .executesPlayer((sender, args) -> {
                            var warpId = (String) args[0];

                            if (!Warps.getInstance().hasWarp(warpId)) {
                                sender.sendMessage("Invalid warp!");
                                return;
                            }

                            Warps.getInstance().removeWarp(warpId);

                            sender.sendMessage("Removed warp " + warpId);
                        })
                );
    }
}