package co.runed.bolster;

import co.runed.bolster.state.ScheduledStateSeries;
import co.runed.bolster.state.State;
import co.runed.bolster.state.StateSeries;
import org.bukkit.plugin.Plugin;

public class GameManager {
    public StateSeries mainState;

    public boolean hasStarted = false;

    public GameManager(Plugin plugin) {
        this.mainState = new ScheduledStateSeries(plugin);
    }

    public StateSeries getMainState() {
        return this.mainState;
    }

    public State getCurrentState() {
        return this.mainState.getCurrent();
    }

    public boolean isCurrentState(Class<? extends State> state) {
        return this.getCurrentState().getClass() == state;
    }

    public void addState(State state) {
        this.mainState.add(state);
    }

    public void start() {
        this.hasStarted = true;

        this.mainState.start();
    }
}
