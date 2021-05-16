package co.runed.bolster.math;

public class NumberUtil
{
    public static Number addNumbers(Number a, Number b)
    {
        if (a instanceof Double || b instanceof Double)
        {
            return a.doubleValue() + b.doubleValue();
        }
        else if (a instanceof Float || b instanceof Float)
        {
            return a.floatValue() + b.floatValue();
        }
        else if (a instanceof Long || b instanceof Long)
        {
            return a.longValue() + b.longValue();
        }
        else
        {
            return a.intValue() + b.intValue();
        }
    }

    public static Number subtractNumbers(Number a, Number b)
    {
        if (a instanceof Double || b instanceof Double)
        {
            return a.doubleValue() - b.doubleValue();
        }
        else if (a instanceof Float || b instanceof Float)
        {
            return a.floatValue() - b.floatValue();
        }
        else if (a instanceof Long || b instanceof Long)
        {
            return a.longValue() - b.longValue();
        }
        else
        {
            return a.intValue() - b.intValue();
        }
    }

    public static Number multiplyNumbers(Number a, Number b)
    {
        if (a instanceof Double || b instanceof Double)
        {
            return a.doubleValue() * b.doubleValue();
        }
        else if (a instanceof Float || b instanceof Float)
        {
            return a.floatValue() * b.floatValue();
        }
        else if (a instanceof Long || b instanceof Long)
        {
            return a.longValue() * b.longValue();
        }
        else
        {
            return a.intValue() * b.intValue();
        }
    }

    public static Number divideNumbers(Number a, Number b)
    {
        if (a instanceof Double || b instanceof Double)
        {
            return a.doubleValue() / b.doubleValue();
        }
        else if (a instanceof Float || b instanceof Float)
        {
            return a.floatValue() / b.floatValue();
        }
        else if (a instanceof Long || b instanceof Long)
        {
            return a.longValue() / b.longValue();
        }
        else
        {
            return a.intValue() / b.intValue();
        }
    }
}
