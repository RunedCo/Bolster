package co.runed.bolster.commands;

import co.runed.bolster.Permissions;
import co.runed.bolster.gui.GuiServers;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;

import java.util.Collections;

public class CommandServerGUI extends CommandBase {
    public CommandServerGUI() {
        super("servers");

    }

    @Override
    public CommandAPICommand build() {
        return new CommandAPICommand(this.command)
                .withPermission(Permissions.COMMAND_SERVERS)
                .withArguments(new StringArgument("gamemode"))
                .executesPlayer(((sender, args) -> {
                    var gamemode = (String) args[0];

                    new GuiServers(null, Collections.singletonList(gamemode)).show(sender);
                }));
    }
}