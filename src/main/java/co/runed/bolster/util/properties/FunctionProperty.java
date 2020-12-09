package co.runed.bolster.util.properties;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class FunctionProperty<T> extends Property<T>
{
    Function<Properties, T> getFunction;
    BiConsumer<Properties, T> setFunction;

    public FunctionProperty(String id, Function<Properties, T> getFunction, BiConsumer<Properties, T> setFunction)
    {
        super(id);

        this.getFunction = getFunction;
        this.setFunction = setFunction;
    }

    @Override
    public T getDefault()
    {
        return null;
    }
}
