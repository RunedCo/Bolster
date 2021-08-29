package co.runed.bolster;

import co.runed.bolster.util.BukkitUtil;
import co.runed.bolster.util.ItemBuilder;
import co.runed.bolster.util.Manager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Warps extends Manager {
    private final YamlConfiguration config;
    private File configFile = new File(plugin.getDataFolder(), "warps.yml");

    private Map<String, Warp> warps = new HashMap<>();

    private static Warps _instance;

    public Warps(Plugin plugin) {
        super(plugin);

        this.config = new YamlConfiguration();

        try {
            if (!configFile.exists()) {
                plugin.saveResource("warps.yml", false);
            }

            this.config.load(configFile);

            this.load();
        }
        catch (Exception e) {

        }

        _instance = this;
    }

    private void load() {
        if (!this.config.isConfigurationSection("warps")) return;

        var warps = this.config.getConfigurationSection("warps");

        for (var key : warps.getKeys(false)) {
            if (warps.isConfigurationSection(key)) {
                var warpConfig = warps.getConfigurationSection(key);

                var warp = new Warp(key, BukkitUtil.stringToLocation(warpConfig.getString("location")));
                warp.icon = Material.valueOf(warpConfig.getString("icon", warp.icon.name()));
                warp.name = warpConfig.getString("name", warp.name);

                this.warps.put(key, warp);
            }
        }
    }

    private void save() {
        var warps = this.config.createSection("warps");

        for (var warp : this.warps.values()) {
            if (!warp.save) continue;

            warps.set(warp.id, warp.serialize());
        }

        this.config.set("warps", warps);

        try {
            this.config.save(configFile);
        }
        catch (Exception e) {
            plugin.getLogger().severe("Error saving warps file!");
            e.printStackTrace();
        }
    }

    public void setIcon(String id, Material icon) {
        if (!this.warps.containsKey(id)) return;

        this.warps.get(id).icon = icon;

        this.save();
    }

    public Material getIcon(String id) {
        if (!this.warps.containsKey(id)) return Material.ENDER_EYE;

        return this.warps.get(id).icon;
    }

    public void setName(String id, String name) {
        if (!this.warps.containsKey(id)) return;

        this.warps.get(id).name = name;

        this.save();
    }

    public String getName(String id) {
        if (!this.warps.containsKey(id)) return null;

        return this.warps.get(id).name;
    }

    public void setSave(String id, boolean save) {
        if (!this.warps.containsKey(id)) return;

        this.warps.get(id).save = save;

        this.save();
    }

    public void addWarp(String id, Location location) {
        this.warps.put(id, new Warp(id, location));

        this.save();
    }

    public void removeWarp(String id) {
        this.warps.remove(id);

        this.save();
    }

    public boolean hasWarp(String id) {
        return this.warps.containsKey(id);
    }

    public Warp getWarp(String id) {
        if (!this.hasWarp(id)) return null;

        return this.warps.get(id);
    }

    public Map<String, Warp> getWarps() {
        return warps;
    }

    public static Warps getInstance() {
        return _instance;
    }

    public static class Warp implements ConfigurationSerializable {
        public final Location location;

        public String id;
        public String name = null;
        public Material icon = Material.ENDER_EYE;

        private boolean save = true;

        private Warp(String id, Location location) {
            this.id = id;
            this.location = location;
        }

        public ItemStack getIcon() {
            var builder = new ItemBuilder(this.icon)
                    .setDisplayName(Component.text((name == null ? id : name), NamedTextColor.WHITE))
                    .addLore(ChatColor.GRAY + "/warp " + id);

            return builder.build();
        }

        public void teleport(LivingEntity entity) {
            entity.teleport(location);
        }

        @Override
        public @NotNull Map<String, Object> serialize() {
            Map<String, Object> map = new HashMap<>();

            if (name != null) map.put("name", name);
            map.put("icon", icon.name());
            map.put("location", BukkitUtil.locationToString(location));

            return map;
        }
    }
}
