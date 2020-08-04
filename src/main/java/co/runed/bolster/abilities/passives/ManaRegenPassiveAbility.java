package co.runed.bolster.abilities.passives;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.properties.Properties;

public class ManaRegenPassiveAbility extends Ability
{
    float manaPerSecond = 0;

    public ManaRegenPassiveAbility(float manaPerSecond)
    {
        super();

        this.setCooldown(0.5d);

        this.manaPerSecond = manaPerSecond;
    }

    @Override
    public void onActivate(Properties properties)
    {
        Bolster.getManaManager().addCurrentMana(properties.get(AbilityProperties.CASTER), this.manaPerSecond / 2);
    }

    public float getManaPerSecond()
    {
        return this.manaPerSecond;
    }

    public void setManaPerSecond(float manaPerSecond)
    {
        this.manaPerSecond = manaPerSecond;
    }
}
