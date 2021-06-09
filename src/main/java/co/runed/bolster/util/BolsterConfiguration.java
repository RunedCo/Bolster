package co.runed.bolster.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BolsterConfiguration extends YamlConfiguration
{
    @Override
    public Object get(String path, Object def)
    {
        Object value = super.get(path, def);

        this.set(path, value);

        return value;
    }

    @Override
    public boolean isString(String path)
    {
        try
        {
            String test = this.getString(path);
        }
        catch (Exception e)
        {
            return false;
        }

        return true;
    }

    @Override
    public boolean isInt(String path)
    {
        try
        {
            Integer test = this.getInt(path);
        }
        catch (Exception e)
        {
            return false;
        }

        return true;
    }

    @Override
    public boolean isDouble(String path)
    {
        try
        {
            Double test = this.getDouble(path);
        }
        catch (Exception e)
        {
            return false;
        }

        return true;
    }

    @Override
    public boolean isLong(String path)
    {
        try
        {
            Long test = this.getLong(path);
        }
        catch (Exception e)
        {
            return false;
        }

        return true;
    }

    @Override
    public String getString(String path, String def)
    {
        if (this.isList(path))
        {
            return String.join(StringUtil.join(StringUtil.NEW_LINE, this.getStringList(path)));
        }

        return super.getString(path, def);
    }

    public String getColorString(String path)
    {
        return getColorString(path, null);
    }

    public String getColorString(String path, String def)
    {
        String value = this.getString(path, def);

        if (value == null) return null;

        return ChatColor.translateAlternateColorCodes('&', value);
    }

    @Override
    public int getInt(String path, int def)
    {
        if (this.isList(path) && this.getList(path).size() >= 2)
        {
            List<Integer> range = this.getIntegerList(path);

            return ThreadLocalRandom.current().nextInt(range.get(0), range.get(1) + 1);
        }

        return super.getInt(path, def);
    }

    @Override
    public double getDouble(String path, double def)
    {
        if (this.isList(path) && this.getList(path).size() >= 2)
        {
            List<Double> range = this.getDoubleList(path);

            return ThreadLocalRandom.current().nextDouble(range.get(0), range.get(1) + 1);
        }

        return super.getDouble(path, def);
    }

    @Override
    public long getLong(String path, long def)
    {
        if (this.isList(path) && this.getList(path).size() >= 2)
        {
            List<Long> range = this.getLongList(path);

            return ThreadLocalRandom.current().nextLong(range.get(0), range.get(1) + 1);
        }

        return super.getLong(path, def);
    }

    public Component getComponent(String path)
    {
        return getComponent(path, null);
    }

    public Component getComponent(String path, Component def)
    {
        if (!isComponent(path))
        {
            return def;
        }

        String serialized = this.getString(path);

        return MiniMessage.get().parse(serialized);
    }

    public void setComponent(String path, Component value)
    {
        final String json = MiniMessage.get().serialize(value);

        this.set(path, json);
    }

    public boolean isComponent(String path)
    {
        String serialized = this.getString(path);
        Component component = MiniMessage.get().deseializeOrNull(serialized);

        return component != null;
    }
}
