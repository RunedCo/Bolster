package co.runed.bolster.commands;

import co.runed.bolster.Bolster;
import co.runed.bolster.Permissions;
import co.runed.dayroom.properties.Property;

public class CommandGameProperty extends CommandProperty {
    public CommandGameProperty(Property<?> property) {
        super("gameproperty", "Game Property", property, Permissions.COMMAND_GAME_PROPERTIES);
    }

    @Override
    public Object get() {
        return Bolster.getActiveGameMode().getProperties().get(property);
    }

    @Override
    public void set(Object value) {
        Bolster.getActiveGameMode().getProperties().setUnsafe(property, value);
    }
}
