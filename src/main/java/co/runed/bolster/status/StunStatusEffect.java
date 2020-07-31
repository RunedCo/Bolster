package co.runed.bolster.status;

import org.bukkit.entity.LivingEntity;

public class StunStatusEffect extends StatusEffect
{
    public StunStatusEffect(int duration)
    {
        super(1, duration);
    }

    @Override
    public String getName()
    {
        return "Stunned";
    }

    @Override
    public void onStart()
    {

    }

    @Override
    public void onEnd()
    {

    }

    @Override
    public void onTick()
    {

    }
}
