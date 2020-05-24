package co.runed.bolster.commands;

import io.github.jorelali.commandapi.api.CommandAPI;
import io.github.jorelali.commandapi.api.CommandPermission;
import io.github.jorelali.commandapi.api.arguments.Argument;
import io.github.jorelali.commandapi.api.exceptions.WrapperCommandSyntaxException;
import org.bukkit.command.CommandSender;

import java.util.LinkedHashMap;

public class CommandBase
{
    public String command;
    public String[] aliases = new String[0];
    public CommandPermission permission = CommandPermission.NONE;
    public LinkedHashMap<String, Argument> arguments = new LinkedHashMap<>();
    private String commandNamespace = "minecraft";

    public CommandBase(String command, String permission, String[] aliases, LinkedHashMap<String, Argument> arguments)
    {
        this.command = command;
        if (aliases != null) this.aliases = aliases;
        if (permission != null) this.permission = CommandPermission.fromString(permission);
        if (arguments != null) this.arguments = arguments;
    }

    public void run(CommandSender sender, Object[] args) throws WrapperCommandSyntaxException
    {
    }

    public void register()
    {
        CommandAPI.getInstance().register(this.command, this.permission, this.aliases, this.arguments, this::run);
    }

    public void setCommandNamespace(String namespace) {
        this.commandNamespace = namespace;
    }
}
