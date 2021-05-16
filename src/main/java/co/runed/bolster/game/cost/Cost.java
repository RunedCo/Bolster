package co.runed.bolster.game.cost;

import co.runed.bolster.util.properties.Properties;

public abstract class Cost
{
    public abstract boolean evaluate(Properties properties);

    public abstract boolean run(Properties properties);
}
