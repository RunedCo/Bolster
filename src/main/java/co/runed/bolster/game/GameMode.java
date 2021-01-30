package co.runed.bolster.game;

import co.runed.bolster.events.GameModePausedEvent;
import co.runed.bolster.game.state.State;
import co.runed.bolster.game.state.StateSeries;
import co.runed.bolster.util.Manager;
import co.runed.bolster.util.properties.Property;
import co.runed.bolster.util.registries.IRegisterable;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

public abstract class GameMode extends Manager implements IRegisterable
{
    public static final Property<Double> XP_MULTIPLER = new Property<>("xp_multiplier", 1.0);
    public static final Property<Double> GOLD_MULTIPLER = new Property<>("gold_multiplier", 1.0);
    public static final Property<Double> DAMAGE_MULTIPLIER = new Property<>("damage_multiplier", 1.0);
    public static final Property<Double> HEALTH_MULTIPLIER = new Property<>("health_multiplier", 1.0);

    StateSeries mainState;
    String id;

    GameProperties properties;

    boolean hasStarted = false;
    boolean paused = false;

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

    public boolean hasStarted()
    {
        return hasStarted;
    }

    public GameProperties getProperties()
    {
        return this.properties;
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
}
