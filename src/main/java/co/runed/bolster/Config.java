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
    public String serverName = null;
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
        redisHost = redis.getString("host", redisHost);
        redisPort = redis.getInt("port", redisPort);

        gameMode = this.config.getString("gamemode", "bolster");
        serverId = this.config.getString("server-id", serverId);
        serverName = this.config.getString("server-name", serverName);

        hidden = config.getBoolean("hidden", hidden);

        mapSpawn = BukkitUtil.stringToLocation(config.getString("map-spawn", "0,0,0"));
        Warps.getInstance().addWarp("spawn", mapSpawn);
        Warps.getInstance().setName("spawn", "Map Spawn");
        Warps.getInstance().setSave("spawn", false);

        debugMode = this.config.getBoolean("debug", debugMode);

        premiumSlots = this.config.getInt("premium-slots", premiumSlots);

        cleanupPlayers = this.config.getBoolean("cleanup-players", cleanupPlayers);
        cleanupFrequency = this.config.getInt("cleanup-frequency", cleanupFrequency);
        forceCleanupTime = this.config.getInt("force-cleanup-time", forceCleanupTime);

        autoSave = this.config.getBoolean("auto-save", autoSave);
        autoSaveFrequency = this.config.getInt("auto-save-frequency", autoSaveFrequency);
    }

    public Configuration getRawConfig() {
        return config;
    }
}
