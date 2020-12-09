package co.runed.bolster.status;

import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collection;

public class StunStatusEffect extends StatusEffect
{
    public StunStatusEffect(double duration)
    {
        super(duration);
    }

    @Override
    public String getName()
    {
        return "Stunned";
    }

    @Override
    public boolean isHard()
    {
        return true;
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
