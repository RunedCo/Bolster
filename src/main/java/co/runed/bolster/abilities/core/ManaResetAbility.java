package co.runed.bolster.abilities.core;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.managers.ManaManager;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.entity.LivingEntity;

public class ManaResetAbility extends Ability
{
    @Override
    public void onActivate(Properties properties)
    {
        LivingEntity caster = properties.get(AbilityProperties.CASTER);
        ManaManager manaManager = Bolster.getManaManager();

        manaManager.setCurrentMana(caster, manaManager.getMaximumMana(caster));
    }
}
