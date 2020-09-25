package co.runed.bolster.settings;

public abstract class Setting<T>
{
    String id;
    T value;

    public Setting(String id, T defaultValue)
    {

    }
}
