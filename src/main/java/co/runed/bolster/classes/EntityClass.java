package co.runed.bolster.classes;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityProvider;

public abstract class EntityClass extends AbilityProvider
{
    private String id;

    public void setId(String id)
    {
        this.id = id;
    }

    @Override
    public String getId()
    {
        return this.id;
    }

    @Override
    public void onCastAbility(Ability ability, Boolean success)
    {

    }

    public void destroy()
    {

    }
}
