package co.runed.bolster.commands;

import co.runed.bolster.Permissions;
import co.runed.bolster.gui.GuiShop;
import co.runed.bolster.util.registries.Registries;
import co.runed.bolster.util.registries.Registry;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.command.CommandSender;

public class CommandShop extends CommandBase {
    public CommandShop() {
        super("shop");
    }

    private String[] getSuggestions(CommandSender sender) {
        return Registries.SHOPS.getEntries().values().stream().map(Registry.Entry::getId).toArray(String[]::new);
    }

    @Override
    public CommandAPICommand build() {
        return new CommandAPICommand(this.command)
                .withPermission(Permissions.COMMAND_SHOP)
                .withArguments(new StringArgument("shop").overrideSuggestions(this::getSuggestions))
                .executesPlayer((sender, args) -> {
                    var id = (String) args[0];

                    new GuiShop(null, Registries.SHOPS.get(id)).show(sender);
                });
    }
}

