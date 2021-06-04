package co.runed.bolster.game;

import co.runed.bolster.Bolster;
import co.runed.bolster.Config;
import co.runed.bolster.events.GameModePauseChangeEvent;
import co.runed.bolster.game.state.State;
import co.runed.bolster.game.state.StateSeries;
import co.runed.bolster.managers.PlayerManager;
import co.runed.bolster.util.IConfigurable;
import co.runed.bolster.managers.Manager;
import co.runed.bolster.util.TimeUtil;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.properties.Property;
import co.runed.bolster.util.registries.IRegisterable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.plugin.Plugin;

import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;

public abstract class GameMode extends Manager implements IRegisterable, IConfigurable
{
    public static final Property<Double> XP_MULTIPLER = new Property<>("xp_multiplier", 1.0);
    public static final Property<Double> GOLD_MULTIPLER = new Property<>("gold_multiplier", 1.0);
    public static final Property<Double> DAMAGE_MULTIPLIER = new Property<>("damage_multiplier", 1.0);
    public static final Property<Double> HEALTH_MULTIPLIER = new Property<>("health_multiplier", 1.0);

    StateSeries mainState;
    String id;
    GameProperties properties;
    HashMap<UUID, Properties> statistics = new HashMap<>();
    Properties globalStatistics = new Properties();

    boolean hasStarted = false;
    boolean paused = false;
    boolean requiresResourcePack = false;

    public GameMode(String id, Plugin plugin)
    {
        super(plugin);

        this.id = id;
        this.properties = new GameProperties();
    }

    public abstract String getName();

    public StateSeries getMainState()
    {
        return this.mainState;
    }

    public void setState(StateSeries mainState)
    {
        this.mainState = mainState;
    }

    public State getCurrentState()
    {
        return this.mainState.getCurrent();
    }

    public boolean isCurrentState(Class<? extends State> state)
    {
        return this.getCurrentState().getClass() == state;
    }

    public void start()
    {
        if (this.hasStarted) return;

        this.hasStarted = true;
        this.mainState.start();
    }

    public boolean isPaused()
    {
        return paused;
    }

    public void setPaused(boolean paused)
    {
        this.paused = paused;

        GameModePauseChangeEvent event = new GameModePauseChangeEvent(paused);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    public void setRequiresResourcePack(boolean requiresResourcePack)
    {
        this.requiresResourcePack = requiresResourcePack;
    }

    public boolean getRequiresResourcePack()
    {
        return requiresResourcePack;
    }

    public boolean hasStarted()
    {
        return hasStarted;
    }

    public GameProperties getProperties()
    {
        return this.properties;
    }

    public <T> T getStatistic(Player player, Property<T> statistic)
    {
        this.statistics.putIfAbsent(player.getUniqueId(), new Properties());

        return this.statistics.get(player.getUniqueId()).get(statistic);
    }

    // TODO better statistics?
    public <T> void setStatistic(Player player, Property<Integer> statistic, int value)
    {
        this.statistics.putIfAbsent(player.getUniqueId(), new Properties());

        this.statistics.get(player.getUniqueId()).set(statistic, value);
    }

    // TODO better statistics?
    public <T> void incrementStatistic(Player player, Property<Integer> statistic, int increment)
    {
        this.statistics.putIfAbsent(player.getUniqueId(), new Properties());

        int value = this.statistics.get(player.getUniqueId()).get(statistic);

        this.statistics.get(player.getUniqueId()).set(statistic, value + increment);
    }

    public <T> T getGlobalStatistic(Property<T> statistic)
    {
        return this.globalStatistics.get(statistic);
    }

    public <T> void setGlobalStatistic(Property<T> statistic, T value)
    {
        this.globalStatistics.set(statistic, value);
    }

    public <T> void incrementGlobalStatistic(Property<Integer> statistic, int increment)
    {
        int value = this.globalStatistics.get(statistic);

        this.globalStatistics.set(statistic, value + increment);
    }

    @Override
    public String getId()
    {
        return this.id;
    }

    @Override
    public String getDescription()
    {
        return null;
    }

    @Override
    public void create(ConfigurationSection config)
    {

    }

    @EventHandler
    public void onResourcePackFailed(PlayerResourcePackStatusEvent event)
    {
        if (event.getStatus() == PlayerResourcePackStatusEvent.Status.DECLINED || event.getStatus() == PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD)
        {
            event.getPlayer().kickPlayer("You need to enable resource packs.");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Config bolsterConfig = Bolster.getBolsterConfig();
        Player player = event.getPlayer();

        player.setPlayerListHeader(ChatColor.YELLOW + "  Welcome to " + bolsterConfig.longGameName + "  \n");

        PlayerData playerData = PlayerManager.getInstance().getPlayerData(player);
        if (playerData.isPremium())
        {
            Instant expiryTime = playerData.getPremiumExpiryTime();
            String footer = ChatColor.AQUA + "  Thank you for supporting the server!  ";

            if (expiryTime.isAfter(Instant.now()))
            {
                footer += "\n\nYour " + bolsterConfig.premiumMembershipName + " expires in\n";
                footer += TimeUtil.formatInstantAsPrettyTimeLeft(expiryTime);
            }

            player.setPlayerListFooter("\n" + footer);
        }
        else
        {
            player.setPlayerListFooter("\n" + ChatColor.AQUA + "Support the server at " + ChatColor.GOLD + ChatColor.BOLD + bolsterConfig.storeUrl + "!");
        }
    }
}
