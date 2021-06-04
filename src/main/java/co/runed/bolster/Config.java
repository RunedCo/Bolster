package co.runed.bolster;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config
{
    private final YamlConfiguration config;

    public String databaseUrl;
    public String databasePort;
    public String databaseUsername;
    public String databasePassword;
    public String databaseName;

    public String gameMode;

    public boolean debugMode = false;

    public String gameName;
    public String shortGameName;
    public String longGameName;
    public String ipAddress;
    public String websiteUrl;
    public String storeUrl;

    public String premiumMembershipName;

    public int premiumSlots = 10;

    public Config() throws IOException, InvalidConfigurationException
    {
        Bolster bolster = Bolster.getInstance();

        File configFile = new File(bolster.getDataFolder(), "config.yml");

        if (!configFile.exists())
        {
            bolster.saveDefaultConfig();
        }

        this.config = new YamlConfiguration();

        this.config.load(configFile);

        ConfigurationSection database = this.config.getConfigurationSection("database");
        this.databaseUrl = database.getString("url", "localhost");
        this.databasePort = database.getString("port", "27071");
        this.databaseUsername = database.getString("username", "admin");
        this.databasePassword = database.getString("password", "admin");
        this.databaseName = database.getString("database", "bolster");

        this.gameMode = this.config.getString("gamemode", "bolster");

        this.debugMode = this.config.getBoolean("debug", this.debugMode);

        this.gameName = ChatColor.translateAlternateColorCodes('&', this.config.getString("game-name", this.gameName));
        this.shortGameName = ChatColor.translateAlternateColorCodes('&', this.config.getString("short-game-name", this.shortGameName));
        this.longGameName = ChatColor.translateAlternateColorCodes('&', this.config.getString("long-game-name", this.longGameName));
        this.ipAddress = ChatColor.translateAlternateColorCodes('&', this.config.getString("ip-address", this.ipAddress));
        this.websiteUrl = ChatColor.translateAlternateColorCodes('&', this.config.getString("website-url", this.websiteUrl));
        this.storeUrl = ChatColor.translateAlternateColorCodes('&', this.config.getString("store-url", this.storeUrl));

        this.premiumMembershipName = ChatColor.translateAlternateColorCodes('&', this.config.getString("premium-member-name", this.premiumMembershipName));
        this.premiumSlots = this.config.getInt("premium-slots", this.premiumSlots);
    }

    public Configuration getRawConfig()
    {
        return config;
    }
}
