package co.runed.bolster.wip.settings;

import co.runed.bolster.util.properties.Property;

public class Setting<T> extends Property<T>
{
    public Setting(String id)
    {
        super(id);
    }

    public Setting(String id, T defaultValue)
    {
        super(id, defaultValue);
    }
}
