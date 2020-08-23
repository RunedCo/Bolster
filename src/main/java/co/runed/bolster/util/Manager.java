package co.runed.bolster.util;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public abstract class Manager implements Listener
{
    Plugin plugin;

    public Manager(Plugin plugin)
    {
        this.plugin = plugin;
    }
}
