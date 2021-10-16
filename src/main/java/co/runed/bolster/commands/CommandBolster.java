package co.runed.bolster.commands;

import co.runed.bolster.Bolster;
import co.runed.bolster.Permissions;
import co.runed.bolster.entity.BolsterEntity;
import co.runed.bolster.game.Settings;
import dev.jorel.commandapi.CommandAPICommand;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

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
                .withSubcommand(new CommandAPICommand("printitem")
                        .executesPlayer((player, args) -> {
                            var item = player.getInventory().getItemInMainHand();

                            player.sendMessage(item.toString());
                        }))
                .withSubcommand(new CommandAPICommand("tps")
                        .executesPlayer((player, args) -> {
                            player.sendMessage("Current TPS: " + Bukkit.getTPS()[0]);
                        }))
                .withSubcommand(new CommandAPICommand("debug")
                        .executesPlayer((player, args) -> {
                            var be = BolsterEntity.from(player);
                            var data = be.getPlayerData();
                            var enabled = data.getSetting(Settings.DEBUG_MODE);
                            data.setSetting(Settings.DEBUG_MODE, !enabled);

                            player.sendMessage("Debug mode enabled: " + !enabled);
                        }));
    }
}