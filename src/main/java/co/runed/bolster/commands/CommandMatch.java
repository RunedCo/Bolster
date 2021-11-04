package co.runed.bolster.commands;

import co.runed.bolster.Bolster;
import co.runed.bolster.Permissions;
import dev.jorel.commandapi.CommandAPICommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;

public class CommandMatch extends CommandBase {
    public CommandMatch() {
        super("match");
    }

    @Override
    public CommandAPICommand build() {
        return new CommandAPICommand(this.command)
                .withPermission(Permissions.COMMAND_MATCH)
                .withSubcommand(new CommandAPICommand("id")
                        .withPermission(Permissions.COMMAND_MATCH + ".id")
                        .executes((sender, args) -> {
                            var matchHistory = Bolster.getActiveGameMode().getMatchHistory();

                            if (!matchHistory.isStarted() || matchHistory.getId() == null) {
                                sender.sendMessage("Match not started");
                                return;
                            }

                            var matchId = matchHistory.getId().toString();

                            sender.sendMessage(Component.text("Match ID is " + matchId)
                                    .append(Component.text()
                                            .color(NamedTextColor.AQUA)
                                            .clickEvent(ClickEvent.openUrl(Bolster.getBolsterConfig().matchHistoryUrl + "/" + matchId))
                                    )
                            );
                        }))
                .withSubcommand(new CommandAPICommand("save")
                        .withPermission(Permissions.COMMAND_MATCH + ".save")
                        .executes((sender, args) -> {
                            var matchHistory = Bolster.getActiveGameMode().getMatchHistory();

                            if (!matchHistory.isStarted() || matchHistory.getId() == null) {
                                sender.sendMessage("Match not started");
                                return;
                            }

                            matchHistory.save();

                            sender.sendMessage(Component.text("Saving..."));
                        }));
    }
}
