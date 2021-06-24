package co.runed.bolster.game.cost;

import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.entity.BolsterEntity;
import co.runed.bolster.managers.ManaManager;
import co.runed.bolster.util.properties.Properties;

public class ManaCost extends Cost
{
    float cost = 0;

    public ManaCost(float cost)
    {
        this.cost = cost;
    }

    @Override
    public boolean evaluate(Properties properties)
    {
        return ManaManager.getInstance().hasEnoughMana(properties.get(AbilityProperties.CASTER).getBukkit(), this.cost);
    }

    @Override
    public boolean run(Properties properties)
    {
        ManaManager manager = ManaManager.getInstance();
        BolsterEntity caster = properties.get(AbilityProperties.CASTER);

        if (this.cost <= 0) return true;

        if (manager.getCurrentMana(caster.getBukkit()) < this.cost)
        {
            return false;
        }

        manager.addCurrentMana(caster.getBukkit(), -this.cost);

        return true;
    }

    @Override
    public String getErrorMessage(Properties properties)
    {
        return "You don't have enough mana!";
    }
}
