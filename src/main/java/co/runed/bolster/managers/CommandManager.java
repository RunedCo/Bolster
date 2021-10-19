package co.runed.bolster.managers;

import co.runed.bolster.commands.CommandBase;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.arguments.*;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    public static final Map<Class<?>, Supplier<? extends Argument>> ARGUMENT_MAP = Map.copyOf(Map.ofEntries(
            Map.entry(int.class, () -> new IntegerArgument("value")),
            Map.entry(long.class, () -> new LongArgument("value")),
            Map.entry(float.class, () -> new FloatArgument("value")),
            Map.entry(double.class, () -> new DoubleArgument("value")),
            Map.entry(Integer.class, () -> new IntegerArgument("value")),
            Map.entry(Long.class, () -> new LongArgument("value")),
            Map.entry(Float.class, () -> new FloatArgument("value")),
            Map.entry(Double.class, () -> new DoubleArgument("value")),
            Map.entry(Location.class, () -> new LocationArgument("value")),
            Map.entry(boolean.class, () -> new BooleanArgument("value")),
            Map.entry(Boolean.class, () -> new BooleanArgument("value")))
    );
}
