package co.runed.bolster.commands;

import co.runed.bolster.managers.CommandManager;
import co.runed.dayroom.properties.Property;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class CommandPlayerProperty extends CommandBase {
    private final String typeName;
    protected final Property<?> property;
    private final String permission;

    public CommandPlayerProperty(String command, String typeName, Property<?> property, String permission) {
        super(command);

        this.typeName = typeName;
        this.property = property;
        this.permission = permission;
    }

    private Argument getArgumentFromProperty() {
        var defValue = property.getDefault();

        return CommandManager.ARGUMENT_MAP.getOrDefault(defValue.getClass(), () -> new StringArgument("value")).get();
    }

    public abstract Object get(Player player);

    public void postGet(CommandSender sender, Object value, Player player) {

    }

    public abstract void set(Player player, Object value);

    public void postSet(CommandSender sender, Player player, Object value) {

    }

    @Override
    public CommandAPICommand build() {
        return new CommandAPICommand(this.command)
                .withPermission(permission + "." + property.getId())
                .withSubcommand(new CommandAPICommand("get")
                        .withArguments(new PlayerArgument("player"), new LiteralArgument(property.getId()))
                        .executes((sender, args) -> {
                            var player = (Player) args[0];
                            var value = get(player);

                            sender.sendMessage(Component.text(typeName + " ")
                                    .append(Component.text(property.getId(), NamedTextColor.AQUA))
                                    .append(Component.text(" is currently set to "))
                                    .append(Component.text(value.toString(), NamedTextColor.AQUA))
                                    .append(Component.text(" for "))
                                    .append(player.displayName()));

                            postGet(sender, value, player);
                        })
                )
                .withSubcommand(new CommandAPICommand("set")
                        .withArguments(new PlayerArgument("player"), new LiteralArgument(property.getId()), getArgumentFromProperty())
                        .executes((sender, args) -> {
                            var player = (Player) args[0];
                            var value = args[1];

                            set(player, value);

                            sender.sendMessage(Component.text("Set ")
                                    .append(Component.text(property.getId(), NamedTextColor.AQUA))
                                    .append(Component.text(" to "))
                                    .append(Component.text(value.toString(), NamedTextColor.AQUA))
                                    .append(Component.text(" for "))
                                    .append(player.displayName()));

                            postSet(sender, player, value);
                        })
                );
    }
}
