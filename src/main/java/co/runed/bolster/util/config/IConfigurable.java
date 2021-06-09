package co.runed.bolster.util.config;

import org.bukkit.configuration.ConfigurationSection;

public interface IConfigurable
{
    // TODO RENAME?
    default void setConfig(ConfigurationSection config)
    {

    }

    default ConfigurationSection getConfig()
    {
        return ConfigUtil.create();
    }

    default boolean isConfigSet()
    {
        return true;
    }

    default void create()
    {
        create(this.getConfig());
    }

    // TODO fix?
    void create(ConfigurationSection config);
}
