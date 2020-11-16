package co.runed.bolster.commands;

import co.runed.bolster.classes.TargetDummyClass;
import co.runed.bolster.gui.GuiShopConfirm;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.Location;

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
                    new GuiShopConfirm().show(sender);
                });
    }
}