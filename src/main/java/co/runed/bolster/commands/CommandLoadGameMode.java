package co.runed.bolster.commands;

import co.runed.bolster.Bolster;
import co.runed.bolster.util.registries.Registries;
import co.runed.bolster.util.registries.Registry;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.command.CommandSender;

public class CommandLoadGameMode extends CommandBase
{
    public CommandLoadGameMode()
    {
        super("loadgamemode");
    }

    private String[] getSuggestions(CommandSender sender)
    {
        return Registries.GAME_MODES.getEntries().values().stream().map(Registry.Entry::getId).toArray(String[]::new);
    }

    @Override
    public CommandAPICommand build()
    {
        return new CommandAPICommand(this.command)
                .withPermission("bolster.commands.loadgamemode")
                .withArguments(new StringArgument("gamemode").overrideSuggestions(this::getSuggestions))
                .executes((sender, args) -> {
                    String id = (String) args[0];

                    Bolster.setActiveGameMode(id);

                    sender.sendMessage("Loading Game Mode '" + id + "'...");
                });
    }
}
