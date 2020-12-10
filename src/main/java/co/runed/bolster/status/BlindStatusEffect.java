package co.runed.bolster.status;

import org.bukkit.ChatColor;
import org.bukkit.potion.PotionEffectType;

public class BlindStatusEffect extends StatusEffect
{
    public BlindStatusEffect(double duration)
    {
        super(duration);
    }

    @Override
    public String getName()
    {
        return "Blinded";
    }

    @Override
    public ChatColor getColor()
    {
        return ChatColor.DARK_GRAY;
    }

    @Override
    public void onStart()
    {
        this.addPotionEffect(PotionEffectType.BLINDNESS, 9, true, false, true);
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
