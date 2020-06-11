package co.runed.bolster.managers;

import co.runed.bolster.commands.CommandBase;
import io.github.jorelali.commandapi.api.CommandAPI;

import java.util.ArrayList;
import java.util.List;

public class CommandManager
{
    private String commandNamespace = "minecraft";
    private final List<CommandBase> commands;

    public CommandManager()
    {
        this.commands = new ArrayList<>();
    }

    public void setCommandNamespace(String namespace) {
        this.commandNamespace = namespace;
    }

    public void add(CommandBase command)
    {
        command.setCommandNamespace(this.commandNamespace);

        this.commands.add(command);

        command.register();
    }

    public void deregisterCommands()
    {
        for (CommandBase command : this.commands)
        {
            CommandAPI.getInstance().unregister(command.command);
        }

        this.commands.clear();
    }
}
