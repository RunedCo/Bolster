package co.runed.bolster.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;

public class CommandGameProperties extends CommandBase
{
    public CommandGameProperties()
    {
        super("gameproperties");
    }

    @Override
    public CommandAPICommand build()
    {
        return new CommandAPICommand(this.command)
                .withSubcommand(new CommandAPICommand("group")
                        .withSubcommand(new CommandAPICommand("add")
                                .withArguments(new StringArgument("permission"))
                                .withArguments(new StringArgument("groupName"))
                                .executes((sender, args) -> {
                                    //perm group add code
                                })
                        )
                        .withSubcommand(new CommandAPICommand("remove")
                                .withArguments(new StringArgument("permission"))
                                .withArguments(new StringArgument("groupName"))
                                .executes((sender, args) -> {
                                    //perm group remove code
                                })
                        )
                )
                .withSubcommand(new CommandAPICommand("user")
                        .withSubcommand(new CommandAPICommand("add")
                                .withArguments(new StringArgument("permission"))
                                .withArguments(new StringArgument("userName"))
                                .executes((sender, args) -> {
                                    //perm user add code
                                })
                        )
                        .withSubcommand(new CommandAPICommand("remove")
                                .withArguments(new StringArgument("permission"))
                                .withArguments(new StringArgument("userName"))
                                .executes((sender, args) -> {
                                    //perm user remove code
                                })
                        )
                );
    }
}
