package co.runed.bolster.commands;

import co.runed.bolster.gui.GuiItems;
import dev.jorel.commandapi.CommandAPICommand;

public class CommandItemsGUI extends CommandBase
{
    public CommandItemsGUI()
    {
        super("items");
    }

    @Override
    public CommandAPICommand build()
    {
        return new CommandAPICommand(this.command)
                .withPermission("bolster.commands.items")
                .executesPlayer(((sender, args) -> {
                    new GuiItems(null).show(sender);
                }));
    }
}
