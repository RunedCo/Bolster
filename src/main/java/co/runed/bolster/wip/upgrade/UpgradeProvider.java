package co.runed.bolster.wip.upgrade;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityProvider;

public class UpgradeProvider extends AbilityProvider
{
    String id;

    @Override
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
    public String getDescription()
    {
        return null;
    }

    @Override
    public void onCastAbility(Ability ability, Boolean success)
    {

    }

    @Override
    public void onToggleCooldown(Ability ability)
    {

    }
}
