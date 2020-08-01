package co.runed.bolster.status;

import org.bukkit.ChatColor;
import org.bukkit.potion.PotionEffect;
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
        this.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 9, true, false, true));
    }

    @Override
    public void onEnd()
    {
        this.getEntity().removePotionEffect(PotionEffectType.BLINDNESS);
    }

    @Override
    public void onTick()
    {

    }
}
