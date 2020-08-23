package co.runed.bolster.abilities.costs;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.managers.ManaManager;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.entity.LivingEntity;

public class ManaAbilityCost extends AbilityCost
{
    float cost;

    public ManaAbilityCost(float cost)
    {
        this.cost = cost;
    }

    @Override
    public boolean run(Ability ability, Properties properties)
    {
        ManaManager manager = Bolster.getManaManager();
        LivingEntity caster = properties.get(AbilityProperties.CASTER);

        if (manager.getCurrentMana(caster) < this.cost)
        {
            return false;
        }

        manager.addCurrentMana(caster, -this.cost);

        return true;
    }
}
