package co.runed.bolster.commands;

import co.runed.bolster.gui.GuiServers;
import dev.jorel.commandapi.CommandAPICommand;

public class CommandServerGUI extends CommandBase
{
    public CommandServerGUI()
    {
        super("servers");
    }

    @Override
    public CommandAPICommand build()
    {
        return new CommandAPICommand(this.command)
                .executesPlayer(((sender, args) -> {
                    new GuiServers(null, null).show(sender);
                }));
    }
}