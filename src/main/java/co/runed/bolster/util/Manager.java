package co.runed.bolster.util;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public abstract class Manager implements Listener {
    public Plugin plugin;

    public Manager(Plugin plugin) {
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
}
