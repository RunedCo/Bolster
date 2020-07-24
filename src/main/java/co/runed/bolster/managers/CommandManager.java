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

    /**
     * Register a command
     *
     * @param command the command
     */
    public void add(CommandBase command)
    {
        this.commands.add(command);

        command.register();
    }

    /**
     * Deregister all commands
     */
    public void deregisterCommands()
    {
        for (CommandBase command : this.commands)
        {
            CommandAPI.unregister(command.command);
        }

        this.commands.clear();
    }
}
