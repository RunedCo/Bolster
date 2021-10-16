package co.runed.bolster.commands;

import co.runed.bolster.managers.CommandManager;
import co.runed.dayroom.properties.Property;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

public abstract class CommandProperty extends CommandBase {
    private final String typeName;
    final Property<?> property;
    private final String permission;

    public CommandProperty(String command, String typeName, Property<?> property, String permission) {
        super(command);

        this.typeName = typeName;
        this.property = property;
        this.permission = permission;
    }

    private Argument getArgumentFromProperty() {
        var defValue = property.getDefault();

        return CommandManager.ARGUMENT_MAP.getOrDefault(defValue.getClass(), () -> new StringArgument("value")).get();
    }

    public abstract Object get();

    public void postGet(CommandSender sender, Object value) {

    }

    public abstract void set(Object value);

    public void postSet(CommandSender sender, Object value) {

    }

    @Override
    public CommandAPICommand build() {
        return new CommandAPICommand(this.command)
                .withPermission(permission + "." + property.getId())
                .withSubcommand(new CommandAPICommand("get")
                        .withArguments(new LiteralArgument(property.getId()))
                        .executes((sender, args) -> {
                            var value = get();

                            sender.sendMessage(Component.text(typeName + " ")
                                    .append(Component.text(property.getId(), NamedTextColor.AQUA))
                                    .append(Component.text(" is currently set to "))
                                    .append(Component.text(value.toString(), NamedTextColor.AQUA)));

                            postGet(sender, value);
                        })
                )
                .withSubcommand(new CommandAPICommand("set")
                        .withArguments(new LiteralArgument(property.getId()), getArgumentFromProperty())
                        .executes((sender, args) -> {
                            var value = args[0];

                            set(value);

                            sender.sendMessage(Component.text("Set ")
                                    .append(Component.text(property.getId(), NamedTextColor.AQUA))
                                    .append(Component.text(" to "))
                                    .append(Component.text(value.toString(), NamedTextColor.AQUA)));

                            postSet(sender, value);
                        })
                );
    }
}
