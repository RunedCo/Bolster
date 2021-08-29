package co.runed.bolster;

import co.runed.bolster.util.config.BolsterConfiguration;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Config {
    private final BolsterConfiguration config;

    public String redisHost = "localhost";
    public int redisPort = 6379;

    public String gameMode;
    public String serverId = null;

    public boolean debugMode = false;

    public String gameName;
    public String shortGameName;
    public Component longGameName;
    public String ipAddress;
    public String websiteUrl;
    public String storeUrl;

    public String premiumMembershipName;

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

        if (overrideFiles != null) {
            Arrays.sort(overrideFiles);

            for (var override : overrideFiles) {
                if (override.exists()) {
                    var overrideConfig = YamlConfiguration.loadConfiguration(override);
                    for (var key : overrideConfig.getKeys(false)) {
                        config.set(key, overrideConfig.get(key));
                    }
                }
            }
        }

        var redis = this.config.getConfigurationSection("redis");
        this.redisHost = redis.getString("host", this.redisHost);
        this.redisPort = redis.getInt("port", this.redisPort);

        this.gameMode = this.config.getString("gamemode", "bolster");
        this.serverId = this.config.getString("server-id", this.serverId);

        this.debugMode = this.config.getBoolean("debug", this.debugMode);

        this.gameName = this.config.getColorString("game-name", this.gameName);
        this.shortGameName = this.config.getColorString("short-game-name", this.shortGameName);
        this.longGameName = this.config.getComponent("long-game-name", this.longGameName);
        this.ipAddress = this.config.getColorString("ip-address", this.ipAddress);
        this.websiteUrl = this.config.getColorString("website-url", this.websiteUrl);
        this.storeUrl = this.config.getColorString("store-url", this.storeUrl);

        this.premiumMembershipName = this.config.getColorString("premium-member-name", this.premiumMembershipName);
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
