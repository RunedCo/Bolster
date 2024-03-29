package co.runed.bolster.util.config;

import co.runed.bolster.util.ComponentUtil;
import co.runed.bolster.util.StringUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.concurrent.ThreadLocalRandom;

public class BolsterConfiguration extends YamlConfiguration {
//    public BolsterConfiguration()
//    {
//        super();
//    }
//
//    public BolsterConfiguration(ConfigurationSection configurationSection)
//    {
//        this();
//
//        for (String key : configurationSection.getKeys(false))
//        {
//            this.set(key, configurationSection.get(key));
//        }
//    }

    @Override
    public Object get(String path, Object def) {
        var value = super.get(path, def);

        this.set(path, value);

        return value;
    }

    @Override
    public boolean isString(String path) {
        try {
            var test = this.getString(path);
        }
        catch (Exception e) {
            return false;
        }

        return true;
    }

    @Override
    public boolean isInt(String path) {
        try {
            Integer test = this.getInt(path);
        }
        catch (Exception e) {
            return false;
        }

        return true;
    }

    @Override
    public boolean isDouble(String path) {
        try {
            Double test = this.getDouble(path);
        }
        catch (Exception e) {
            return false;
        }

        return true;
    }

    @Override
    public boolean isLong(String path) {
        try {
            Long test = this.getLong(path);
        }
        catch (Exception e) {
            return false;
        }

        return true;
    }

    @Override
    public String getString(String path, String def) {
        if (this.isList(path)) {
            return String.join(StringUtil.join(StringUtil.NEW_LINE, this.getStringList(path)));
        }

        return super.getString(path, def);
    }

    public String getColorString(String path) {
        return getColorString(path, null);
    }

    public String getColorString(String path, String def) {
        var value = this.getString(path, def);

        if (value == null) return null;

        return ChatColor.translateAlternateColorCodes('&', value);
    }

//    @Override
//    public @Nullable ConfigurationSection getConfigurationSection(@NotNull String path)
//    {
//        ConfigurationSection section = super.getConfigurationSection(path);
//
//        if (section == null) return this.createSection(path);
//
//        if (section instanceof BolsterConfiguration)
//        {
//            return section;
//        }
//
//        BolsterConfiguration value = new BolsterConfiguration(section);
//
//        this.set(path, value);
//
//        return value;
//    }
//
//    @Override
//    public @NotNull ConfigurationSection createSection(@NotNull String path)
//    {
//        BolsterConfiguration value = new BolsterConfiguration(super.createSection(path));
//
//        this.set(path, value);
//
//        return value;
//    }

    @Override
    public int getInt(String path, int def) {
        if (this.isList(path) && this.getList(path).size() >= 2) {
            var range = this.getIntegerList(path);

            return ThreadLocalRandom.current().nextInt(range.get(0), range.get(1) + 1);
        }

        return super.getInt(path, def);
    }

    @Override
    public double getDouble(String path, double def) {
        if (this.isList(path) && this.getList(path).size() >= 2) {
            var range = this.getDoubleList(path);

            return ThreadLocalRandom.current().nextDouble(range.get(0), range.get(1) + 1);
        }

        return super.getDouble(path, def);
    }

    @Override
    public long getLong(String path, long def) {
        if (this.isList(path) && this.getList(path).size() >= 2) {
            var range = this.getLongList(path);

            return ThreadLocalRandom.current().nextLong(range.get(0), range.get(1) + 1);
        }

        return super.getLong(path, def);
    }

    public Component getComponent(String path) {
        return getComponent(path, null);
    }

    public Component getComponent(String path, Component def) {
        if (!isComponent(path)) {
            return def;
        }

        var serialized = this.getString(path);

        return ComponentUtil.richText(serialized);
    }

    public void setComponent(String path, Component value) {
        final var json = MiniMessage.get().serialize(value);

        this.set(path, json);
    }

    public boolean isComponent(String path) {
        var serialized = this.getString(path);
        var component = MiniMessage.get().deseializeOrNull(serialized);

        return component != null;
    }
}
