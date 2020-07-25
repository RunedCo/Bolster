package co.runed.bolster.abilities;

import co.runed.bolster.properties.Properties;

import java.util.ArrayList;
import java.util.List;

public class MultiAbility extends Ability
{
    List<Ability> abilities = new ArrayList<>();

    public MultiAbility addAbility(Ability ability)
    {
        this.abilities.add(ability);

        return this;
    }

    @Override
    public void onActivate(Properties properties)
    {
        for (Ability ability : this.abilities)
        {
            ability.activate(properties);
        }
    }
}
