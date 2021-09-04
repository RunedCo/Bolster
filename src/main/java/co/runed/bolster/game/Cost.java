package co.runed.bolster.game;

import co.runed.bolster.common.properties.Properties;

public abstract class Cost {
    public abstract boolean evaluate(Properties properties);

    public abstract boolean run(Properties properties);

    public abstract String getErrorMessage(Properties properties);

}
