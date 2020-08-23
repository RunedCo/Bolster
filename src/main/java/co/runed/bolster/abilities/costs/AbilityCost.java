package co.runed.bolster.abilities.costs;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.util.properties.Properties;

public abstract class AbilityCost
{
    public abstract boolean run(Ability ability, Properties properties);
}
