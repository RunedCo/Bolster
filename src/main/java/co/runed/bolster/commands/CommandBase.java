package co.runed.bolster.commands;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import org.bukkit.command.CommandSender;

import java.util.LinkedHashMap;

public class CommandBase
{
    public String command;
    public String[] aliases = new String[0];
    public CommandPermission permission = CommandPermission.NONE;
    public LinkedHashMap<String, Argument> arguments = new LinkedHashMap<>();

    private CommandAPICommand commandAPICommand;

    public CommandBase(String command, String permission, String[] aliases, LinkedHashMap<String, Argument> arguments)
    {
        this.command = command;
        if (aliases != null) this.aliases = aliases;
        if (permission != null) this.permission = CommandPermission.fromString(permission);
        if (arguments != null) this.arguments = arguments;
    }

    public void run(CommandSender sender, Object[] args) throws WrapperCommandSyntaxException {
    }

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
