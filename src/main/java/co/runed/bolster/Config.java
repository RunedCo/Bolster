package co.runed.bolster;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config
{
    private YamlConfiguration config;

    public String databaseUrl;
    public String databasePort;
    public String databaseUsername;
    public String databasePassword;
    public String databaseName;

    public boolean debugMode = false;

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

        this.debugMode = this.config.getBoolean("debug", this.debugMode);
    }

    public Configuration getRawConfig()
    {
        return config;
    }
}
