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

    public CommandBase(String command)
    {
        this.command = command;
    }

    public abstract CommandAPICommand build();
}
