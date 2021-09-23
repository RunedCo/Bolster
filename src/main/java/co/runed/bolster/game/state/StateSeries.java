package co.runed.bolster.game.state;

import co.runed.bolster.Bolster;

import java.time.Duration;
import java.util.List;

public class StateSeries extends StateHolder {
    protected int current = 0;
    protected boolean skipping = false;
    protected Class<? extends State> skippingTo = null;

    public void addNext(State state) {
        this.states.add(current + 1, state);
    }

    public void addNext(List<State> newStates) {
        var i = 1;
        for (var state : newStates) {
            this.states.add(current + i, state);
            ++i;
        }
    }

    public void skip() {
        skipping = true;
    }

    public void skipTo(Class<? extends State> stateClass) {
        this.skipping = true;
        this.skippingTo = stateClass;
    }

    public State getCurrent() {
        return this.states.get(current);
    }

    @Override
    public void onStart() {
        if (states.isEmpty()) {
            end();
            return;
        }

        this.states.get(current).start();
        Bolster.getInstance().getLogger().info("STARTING STATE " + this.states.get(current).getClass().toString());
    }

    @Override
    public void onUpdate() {
        var currentState = this.states.get(current);

        currentState.update();

        if ((currentState.isReadyToEnd() && !currentState.getFrozen()) || skipping) {
            currentState.end();
            Bolster.getInstance().getLogger().info("ENDING STATE " + currentState.getClass().toString());

            ++current;

            if (current >= this.states.size()) {
                end();
                this.skipping = false;
                return;
            }

            currentState = this.states.get(current);

            if (this.skipping && (this.skippingTo == null || currentState.getClass() == this.skippingTo)) {
                this.skipping = false;
                this.skippingTo = null;
            }

            currentState.start();
            Bolster.getInstance().getLogger().info("STARTING STATE " + currentState.getClass().toString());
        }
    }

    @Override
    public boolean isReadyToEnd() {
        return (current == this.states.size() - 1 && this.states.get(current).isReadyToEnd());
    }

    @Override
    public void onEnd() {
        if (current < this.states.size()) {
            this.states.get(current).end();
        }
    }

    @Override
    public Duration getDuration() {
        var duration = Duration.ZERO;

        for (var state : this.states) {
            duration = duration.plus(state.getDuration());
        }

        return duration;
    }
}
