package co.runed.bolster.state;

import java.time.Duration;
import java.util.List;

public class StateSeries extends StateHolder {
    protected int current = 0;
    protected boolean skipping = false;

    public void addNext(State state) {
        this.states.add(current + 1, state);
    }

    public void addNext(List<State> newStates) {
        int i = 1;
        for (State state : newStates) {
            this.states.add(current + i, state);
            ++i;
        }
    }

    public void skip() {
        skipping = true;
    }

    public State getCurrent() {
        return this.states.get(current);
    }

    @Override
    public void onStart() {
        if(states.isEmpty()) {
            end();
            return;
        }

        this.states.get(current).start();
    }

    @Override
    public void onUpdate() {
        this.states.get(current).update();

        if((this.states.get(current).isReadyToEnd() && !this.states.get(current).getFrozen()) || skipping) {
            if(skipping) {
                skipping = false;
            }

            this.states.get(current).end();

            ++current;

            if(current >= this.states.size()) {
                end();
                return;
            }

            this.states.get(current).start();
        }
    }

    @Override
    public boolean isReadyToEnd() {
        return (current == this.states.size() - 1 && this.states.get(current).isReadyToEnd());
    }

    @Override
    public void onEnd() {
        if(current < this.states.size())
        {
            this.states.get(current).end();
        }
    }

    @Override
    public Duration getDuration() {
        Duration duration = Duration.ZERO;

        for (State state : this.states) {
            duration = duration.plus(state.getDuration());
        }

        return duration;
    }
}
