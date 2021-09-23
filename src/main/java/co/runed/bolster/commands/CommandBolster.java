package co.runed.bolster.commands;

import co.runed.bolster.Bolster;
import co.runed.bolster.Permissions;
import dev.jorel.commandapi.CommandAPICommand;
import net.kyori.adventure.text.Component;

public class CommandBolster extends CommandBase {
    public CommandBolster() {
        super("bolster");
    }

    @Override
    public CommandAPICommand build() {
        return new CommandAPICommand(this.command)
                .withPermission(Permissions.COMMAND_BOLSTER)
                .withSubcommand(new CommandAPICommand("reload")
                        .executes((sender, args) -> {
                            sender.sendMessage(Component.text("Reloading..."));

                            Bolster.reload();
                        }))
                .withSubcommand(new CommandAPICommand("itemdebug")
                        .executesPlayer((player, args) -> {
                            var item = player.getInventory().getItemInMainHand();

                            player.sendMessage(item.toString());
                        }));
    }
}