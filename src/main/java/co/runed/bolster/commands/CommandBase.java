package co.runed.bolster.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public abstract class CommandBase
{
    public String command;
    public String[] aliases = new String[0];
    public CommandPermission permission = CommandPermission.NONE;
    public List<Argument> arguments = new ArrayList<>();

    private CommandAPICommand commandAPICommand;

    public CommandBase(String command, String permission)
    {
        this.command = command;
        if (permission != null) this.permission = CommandPermission.fromString(permission);
    }

    public abstract void run(CommandSender sender, Object[] args) throws WrapperCommandSyntaxException;

    public void register()
    {
        this.commandAPICommand = new CommandAPICommand(this.command)
                .withArguments(arguments)                     // The arguments
                .withAliases(this.aliases) // Command aliases
                .withPermission(this.permission)         // Required permissions
                .executes(this::run);

        this.commandAPICommand.register();
    }
}
