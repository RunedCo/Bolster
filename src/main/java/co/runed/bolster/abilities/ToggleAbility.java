package co.runed.bolster.abilities;

import co.runed.bolster.properties.Properties;

public abstract class ToggleAbility extends PassiveAbility
{
    boolean active = false;

    public ToggleAbility(long tickInterval)
    {
        super(tickInterval);
    }

    @Override
    public void onActivate(Properties properties)
    {
        this.active = !this.active;

        this.onToggle(this.active, properties);
    }

    public void onToggle(boolean active, Properties properties)
    {

    }

    @Override
    protected void run()
    {
        if (!this.active) return;

        super.run();
    }
}
