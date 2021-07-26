package co.runed.bolster.commands;

import co.runed.bolster.Permissions;
import co.runed.bolster.gui.GuiWarps;
import dev.jorel.commandapi.CommandAPICommand;

public class CommandWarpGUI extends CommandBase
{
    public CommandWarpGUI()
    {
        super("warp");
    }

    @Override
    public CommandAPICommand build()
    {
        return new CommandAPICommand(this.command)
                .withPermission(Permissions.COMMAND_WARP_GUI)
                .executesPlayer(((sender, args) -> {
                    new GuiWarps(null).show(sender);
                }));
    }
}