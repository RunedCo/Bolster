package co.runed.bolster.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class StateHolder extends State
{
    protected List<State> states = new ArrayList<>();

    public void add(State state)
    {
        this.states.add(state);
    }

    public void addAll(Collection<State> states)
    {
        this.states.addAll(states);
    }

    @Override
    public void setFrozen(boolean freeze)
    {
        for (State state : this.states)
        {
            state.setFrozen(freeze);
        }

        super.setFrozen(freeze);
    }
}
