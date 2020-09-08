package co.runed.bolster.game;

import co.runed.bolster.util.Manager;
import co.runed.bolster.util.registries.IRegisterable;
import co.runed.bolster.game.state.State;
import co.runed.bolster.game.state.StateSeries;
import org.bukkit.plugin.Plugin;

public class GameMode extends Manager implements IRegisterable
{
    StateSeries mainState;
    String id;

    GameProperties properties;

    public boolean hasStarted = false;

    public GameMode(String id, Plugin plugin)
    {
        super(plugin);

        this.id = id;
        this.properties = new GameProperties();
    }

    public State getMainState()
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

    public GameProperties getProperties()
    {
        return this.properties;
    }

    @Override
    public void setId(String id)
    {
        this.id = id;
    }

    @Override
    public String getId()
    {
        return this.id;
    }
}
