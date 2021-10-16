package co.runed.bolster.commands;

import co.runed.bolster.Bolster;
import co.runed.dayroom.properties.Property;

public class CommandGameProperty extends CommandProperty {
    public CommandGameProperty(Property<?> property) {
        super("gameprop", "Game Property", property, "");
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
