package co.runed.bolster.util.config;

import org.bukkit.configuration.ConfigurationSection;

public interface Configurable {
    // TODO RENAME?
    default void setConfig(ConfigurationSection config) {

    }

    default ConfigurationSection getConfig() {
        return ConfigUtil.create();
    }

    default boolean isConfigSet() {
        return true;
    }

    void loadConfig(ConfigurationSection config);
}
