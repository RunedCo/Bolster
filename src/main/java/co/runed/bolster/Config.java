package co.runed.bolster;

import co.runed.bolster.util.BukkitUtil;
import co.runed.bolster.util.config.BolsterConfiguration;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class Config {
    private final BolsterConfiguration config;

    public String redisHost = "localhost";
    public int redisPort = 6379;

    public String gameMode;
    public String serverId = null;
    public boolean hidden = false;

    public Location mapSpawn;

    public boolean debugMode = false;

    public int premiumSlots = 10;

    public boolean cleanupPlayers = true;
    public int cleanupFrequency = 12000;
    public int forceCleanupTime = 72000;

    public boolean autoSave = true;
    public int autoSaveFrequency = 1200;

    public Config() throws IOException, InvalidConfigurationException {
        var bolster = Bolster.getInstance();

        var configFile = new File(bolster.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            bolster.saveDefaultConfig();
        }

        this.config = new BolsterConfiguration();
        this.config.load(configFile);

        var overridesDir = bolster.getDataFolder();
        var overrideFiles = overridesDir.listFiles((d, name) -> name.startsWith("overrides") && name.endsWith(".yml"));
        var overrideConfigs = new ArrayList<Configuration>();

        if (overrideFiles != null) {
            Arrays.sort(overrideFiles);

            for (var override : overrideFiles) {
                if (override.exists()) {
                    var overrideConfig = YamlConfiguration.loadConfiguration(override);

                    overrideConfigs.add(overrideConfig);
                }
            }
        }

        overrideConfigs.sort(Comparator.comparingInt(c -> c.getInt("override-priority", 0)));

        for (var overrideConfig : overrideConfigs) {
            for (var key : overrideConfig.getKeys(false)) {
                config.set(key, overrideConfig.get(key));
            }
        }

        var redis = this.config.getConfigurationSection("redis");
        this.redisHost = redis.getString("host", this.redisHost);
        this.redisPort = redis.getInt("port", this.redisPort);

        this.gameMode = this.config.getString("gamemode", "bolster");
        this.serverId = this.config.getString("server-id", this.serverId);
        hidden = config.getBoolean("hidden", hidden);

        this.mapSpawn = BukkitUtil.stringToLocation(config.getString("map-spawn", "0,0,0"));
        Warps.getInstance().addWarp("spawn", this.mapSpawn);
        Warps.getInstance().setName("spawn", "Map Spawn");
        Warps.getInstance().setSave("spawn", false);

        this.debugMode = this.config.getBoolean("debug", this.debugMode);

        this.premiumSlots = this.config.getInt("premium-slots", this.premiumSlots);

        this.cleanupPlayers = this.config.getBoolean("cleanup-players", this.cleanupPlayers);
        this.cleanupFrequency = this.config.getInt("cleanup-frequency", this.cleanupFrequency);
        this.forceCleanupTime = this.config.getInt("force-cleanup-time", this.forceCleanupTime);

        this.autoSave = this.config.getBoolean("auto-save", this.autoSave);
        this.autoSaveFrequency = this.config.getInt("auto-save-frequency", this.autoSaveFrequency);
    }

    public Configuration getRawConfig() {
        return config;
    }
}
