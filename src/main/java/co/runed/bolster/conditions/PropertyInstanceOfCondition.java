package co.runed.bolster.conditions;

import co.runed.bolster.conditions.base.Condition;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.properties.Property;

public class PropertyInstanceOfCondition<T> extends Condition
{
    Property<T> property;
    Class<? extends T> value;

    public PropertyInstanceOfCondition(Property<T> property, Class<? extends T> value)
    {
        this.property = property;
        this.value = value;
    }

    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        return properties.get(this.property).getClass().equals(this.value);
    }

    @Override
    public void onFail(IConditional conditional, Properties properties, boolean inverted)
    {

    }

    @Override
    public String getErrorMessage(IConditional conditional, Properties properties, boolean inverted)
    {
        return null;
    }
}


