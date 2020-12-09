package co.runed.bolster.commands;

import co.runed.bolster.gui.GuiConfirm;
import dev.jorel.commandapi.CommandAPICommand;

public class CommandConfirm extends CommandBase
{
    public CommandConfirm()
    {
        super("confirm");
    }

    @Override
    public CommandAPICommand build()
    {
        return new CommandAPICommand(this.command)
                .withPermission("bolster.commands.confirm")
                .executesPlayer((sender, args) -> {
                    new GuiConfirm(null, null, null).show(sender);
                });
    }
}