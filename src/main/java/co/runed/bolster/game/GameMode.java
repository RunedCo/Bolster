package co.runed.bolster.game;

import co.runed.bolster.events.GameModePausedEvent;
import co.runed.bolster.game.state.State;
import co.runed.bolster.game.state.StateSeries;
import co.runed.bolster.util.IConfigurable;
import co.runed.bolster.managers.Manager;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.properties.Property;
import co.runed.bolster.util.registries.IRegisterable;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.plugin.Plugin;

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

        GameModePausedEvent event = new GameModePausedEvent(paused);
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

    public <T> T getGlobalStatistic(Property<T> statistic)
    {
        return this.globalStatistics.get(statistic);
    }

    public <T> void setGlobalStatistic(Property<T> statistic, T value)
    {
        this.globalStatistics.set(statistic, value);
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
}
