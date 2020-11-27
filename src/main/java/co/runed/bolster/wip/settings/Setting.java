package co.runed.bolster.wip.settings;

public class Setting<T>
{
    String id;
    T defaultValue;

    public Setting(String id, T defaultValue)
    {
        this.id = id;
        this.defaultValue = defaultValue;
    }

    public String getId()
    {
        return id;
    }

    public T getDefaultValue()
    {
        return defaultValue;
    }
}
