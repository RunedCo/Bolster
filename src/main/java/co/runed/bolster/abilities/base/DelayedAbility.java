package co.runed.bolster.abilities.base;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.Ability;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.Bukkit;

public class DelayedAbility extends Ability
{
    double delay;

    public DelayedAbility(double delay)
    {
        super();

        this.delay = delay;
    }

    @Override
    public boolean activate(Properties properties)
    {
        Properties cloned = new Properties(properties);

        Bukkit.getScheduler().runTaskLater(Bolster.getInstance(), () -> super.activate(cloned), (long) (this.delay * 20));

        return true;
    }

    @Override
    public void onActivate(Properties properties)
    {

    }
}
