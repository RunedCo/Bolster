package co.runed.bolster.commands;

import co.runed.bolster.Bolster;
import co.runed.bolster.game.GameMode;
import co.runed.bolster.util.registries.Registries;
import co.runed.bolster.util.registries.Registry;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.command.CommandSender;

public class CommandGame extends CommandBase
{
    public CommandGame()
    {
        super("game");
    }

    private String[] getGameModes(CommandSender sender)
    {
        return Registries.GAME_MODES.getEntries().values().stream().map(Registry.Entry::getId).toArray(String[]::new);
    }

    @Override
    public CommandAPICommand build()
    {
        return new CommandAPICommand(this.command)
                .withPermission("bolster.commands.game")
                .withSubcommand(new CommandAPICommand("pause")
                        .withPermission("bolster.commands.pause")
                        .executes((sender, args) -> {
                            GameMode gameMode = Bolster.getInstance().getActiveGameMode();
                            gameMode.setPaused(!gameMode.isPaused());

                            sender.sendMessage("Set Game Mode " + gameMode.getName() + " to be " + (gameMode.isPaused() ? "paused" : "unpaused"));
                        }))
                .withSubcommand(new CommandAPICommand("loadgamemode")
                        .withPermission("bolster.commands.loadgamemode")
                        .withArguments(new StringArgument("gamemode").overrideSuggestions(this::getGameModes))
                        .executes((sender, args) -> {
                            String id = (String) args[0];

                            Bolster.setActiveGameMode(id);

                            sender.sendMessage("Loading Game Mode '" + id + "'...");
                        }));
    }
}
