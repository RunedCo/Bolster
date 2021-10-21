package co.runed.bolster.commands;

import co.runed.bolster.Permissions;
import co.runed.bolster.gui.GuiServers;
import dev.jorel.commandapi.CommandAPICommand;

import java.util.Collections;

public class CommandServerGUI extends CommandBase {
    public CommandServerGUI() {
        super("servers");
        
    }

    @Override
    public CommandAPICommand build() {
        return new CommandAPICommand(this.command)
                .withPermission(Permissions.COMMAND_SERVERS)
                .executesPlayer(((sender, args) -> {
                    new GuiServers(null, Collections.emptyList()).show(sender);
                }));
    }
}