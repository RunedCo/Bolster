package co.runed.bolster.status;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GroundedStatusEffect extends StatusEffect
{
    public GroundedStatusEffect(int duration)
    {
        super(1, duration);
    }

    @Override
    public String getName()
    {
        return "Grounded";
    }

    @Override
    public ChatColor getColor()
    {
        return ChatColor.GRAY;
    }

    @Override
    public void onStart()
    {
        this.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 128, true, false, false));
    }

    @Override
    public void onEnd()
    {
        this.getEntity().removePotionEffect(PotionEffectType.JUMP);
    }

    @Override
    public void onTick()
    {

    }
}
