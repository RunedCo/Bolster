package co.runed.bolster.abilities.core;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.conditions.IsMaxHealthCondition;
import co.runed.bolster.conditions.NotCondition;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;

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

        this.addCondition(new NotCondition(new IsMaxHealthCondition(Target.CASTER)));
    }

    @Override
    public void onActivate(Properties properties)
    {
        double maxHealth = this.getCaster().getMaxHealth();

        double heal = this.healAmount;

        if (heal < 0) heal = maxHealth - this.getCaster().getHealth();

        this.getCaster().setHealth(Math.min(this.getCaster().getHealth() + heal, maxHealth));
    }
}
