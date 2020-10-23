package co.runed.bolster.abilities.core;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.attribute.Attribute;

public class HealAbility extends Ability
{
    double healAmount;

    public HealAbility()
    {
        this(-1);
    }

    public HealAbility(double healAmount)
    {
        super();

        this.healAmount = healAmount;
    }

    @Override
    public void onActivate(Properties properties)
    {
        double maxHealth = this.getCaster().getMaxHealth();

        double heal = this.healAmount;

        if(heal < 0) heal = maxHealth - this.getCaster().getHealth();

        this.getCaster().setHealth(Math.min(this.getCaster().getHealth() + heal, maxHealth));
    }
}
