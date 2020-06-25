package co.runed.bolster.managers;

import co.runed.bolster.commands.CommandBase;
import dev.jorel.commandapi.CommandAPI;

import java.util.ArrayList;
import java.util.List;

public class CommandManager
{
    private final List<CommandBase> commands;

    public CommandManager()
    {
        this.commands = new ArrayList<>();
    }

    public void add(CommandBase command)
    {
        this.commands.add(command);

        command.register();
    }

    public void deregisterCommands()
    {
        for (CommandBase command : this.commands)
        {
            CommandAPI.unregister(command.command);
        }

        this.commands.clear();
    }
}
