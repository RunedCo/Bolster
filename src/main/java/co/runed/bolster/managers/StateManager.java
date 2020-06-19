package co.runed.bolster.managers;

import co.runed.bolster.state.ScheduledStateSeries;
import co.runed.bolster.state.State;
import co.runed.bolster.state.StateSeries;
import org.bukkit.plugin.Plugin;

public class StateManager {
    public StateSeries mainState;

    public boolean hasStarted = false;

    public StateManager(Plugin plugin) {
        this.mainState = new ScheduledStateSeries(plugin, 10L);
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
