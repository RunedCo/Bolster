package co.runed.bolster.game.traits;

import co.runed.bolster.math.Operation;
import co.runed.bolster.util.properties.Property;

public class Trait<T> extends Property<T>
{
    Operation operation;

    public Trait(String id, Operation operation)
    {
        this(id, null, operation);
    }

    public Trait(String id, T defaultValue, Operation operation)
    {
        super(id, defaultValue);

        this.operation = operation;
    }

    public Operation getOperation()
    {
        return operation;
    }

}
