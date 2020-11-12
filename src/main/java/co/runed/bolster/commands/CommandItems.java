package co.runed.bolster.commands;

import co.runed.bolster.gui.GuiItems;
import dev.jorel.commandapi.CommandAPICommand;

public class CommandItems extends CommandBase
{
    public CommandItems()
    {
        super("items");
    }

    @Override
    public CommandAPICommand build()
    {
        return new CommandAPICommand(this.command)
                .withPermission("bolster.commands.items")
                .executesPlayer(((sender, args) -> {
                    new GuiItems().show(sender);
                }));
    }
}
