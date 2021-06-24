package co.runed.bolster.util.properties;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class FunctionProperty<T> extends Property<T>
{
    Function<Properties, T> getFunction;
    BiConsumer<Properties, T> setFunction;

    public FunctionProperty(String id)
    {
        super(id);
    }

    public FunctionProperty<T> get(Function<Properties, T> getFunction)
    {
        this.getFunction = getFunction;

        return this;
    }

    public FunctionProperty<T> set(BiConsumer<Properties, T> setFunction)
    {
        this.setFunction = setFunction;

        return this;
    }

    @Override
    public T getDefault()
    {
        return null;
    }
}
