package co.runed.bolster.abilities.cost;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.items.Item;
import co.runed.bolster.properties.Properties;

public abstract class AbilityCost
{
    public abstract boolean run(Ability ability, Properties properties);
}
