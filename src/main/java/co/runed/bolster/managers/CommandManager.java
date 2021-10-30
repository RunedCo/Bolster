package co.runed.bolster.managers;

import co.runed.bolster.commands.CommandBase;
import co.runed.bolster.game.Setting;
import co.runed.dayroom.properties.Property;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.arguments.*;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Supplier;

public class CommandManager {
    private final List<CommandBase> commands;

    private static CommandManager _instance;

    public CommandManager() {
        this.commands = new ArrayList<>();

        _instance = this;
    }

    /**
     * Register a command
     *
     * @param command the command
     */
    public void add(CommandBase command) {
        this.commands.add(command);

        command.build().register();
    }

    /**
     * Deregister all commands
     */
    public void deregisterCommands() {
        for (var command : this.commands) {
            CommandAPI.unregister(command.command);
        }

        this.commands.clear();
    }

    public static CommandManager getInstance() {
        return _instance;
    }

    private static String[] getSuggestions(CommandSender sender, Collection<?> values) {
        var items = new ArrayList<>();

        for (var val : values) {
            items.add(val.toString());
        }

        return items.toArray(new String[0]);
    }

    public static Argument getArgumentFromProperty(Property<?> property) {
        var defValue = property.getDefault();

        if (property instanceof Setting<?> setting && setting.hasOptions()) {
            // TODO get setting options
            var settings = new ArrayList<>();

            for (var value : setting.getOptions()) {
                settings.add(value.value);
            }

            return new StringArgument("value").overrideSuggestions((sender -> getSuggestions(sender, settings)));
        }

        if (defValue instanceof Enum enumValue) {
            var possibleValues = enumValue.getDeclaringClass().getEnumConstants();
            var enumSet = EnumSet.allOf(enumValue.getClass());

            return new StringArgument("value").overrideSuggestions((sender -> getSuggestions(sender, enumSet)));
        }

        return CommandManager.ARGUMENT_MAP.getOrDefault(defValue == null ? Object.class : defValue.getClass(), () -> new StringArgument("value")).get();
    }

    public static final Map<Class<?>, Supplier<? extends Argument>> ARGUMENT_MAP = Map.copyOf(Map.ofEntries(
            Map.entry(int.class, () -> new IntegerArgument("value")),
            Map.entry(long.class, () -> new LongArgument("value")),
            Map.entry(float.class, () -> new FloatArgument("value")),
            Map.entry(double.class, () -> new DoubleArgument("value")),
            Map.entry(Integer.class, () -> new IntegerArgument("value")),
            Map.entry(Long.class, () -> new LongArgument("value")),
            Map.entry(Float.class, () -> new FloatArgument("value")),
            Map.entry(Double.class, () -> new DoubleArgument("value")),
            Map.entry(boolean.class, () -> new BooleanArgument("value")),
            Map.entry(Boolean.class, () -> new BooleanArgument("value")),

            Map.entry(Location.class, () -> new LocationArgument("value")),
            Map.entry(EntityType.class, () -> new EntityTypeArgument("value")),
            Map.entry(Player.class, () -> new PlayerArgument("value")))
    );
}
