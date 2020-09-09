package co.runed.bolster.util.cost;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.BolsterEntity;
import co.runed.bolster.managers.ManaManager;
import co.runed.bolster.util.properties.Properties;

public class ManaCost extends Cost
{
    float cost;

    public ManaCost(float cost)
    {
        this.cost = cost;
    }

    @Override
    public boolean run(Properties properties)
    {
        ManaManager manager = Bolster.getManaManager();
        BolsterEntity caster = properties.get(AbilityProperties.CASTER);

        if (manager.getCurrentMana(caster.getBukkit()) < this.cost)
        {
            return false;
        }

        manager.addCurrentMana(caster.getBukkit(), -this.cost);

        return true;
    }
}
