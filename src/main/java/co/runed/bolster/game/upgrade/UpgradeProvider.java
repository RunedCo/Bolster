package co.runed.bolster.game.upgrade;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityProvider;
import co.runed.bolster.abilities.AbilityProviderType;

public class UpgradeProvider extends AbilityProvider
{
    String id;

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
    public AbilityProviderType getType()
    {
        return AbilityProviderType.UPGRADE;
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
