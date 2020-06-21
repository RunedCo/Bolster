package co.runed.bolster.abilities.conditions;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.properties.Properties;

public abstract class Condition {
    public abstract boolean evaluate(Ability ability, Properties properties);

    public void onFail(Ability ability, Properties properties) {

    }
}

