package co.runed.bolster.status;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RootStatusEffect extends StatusEffect
{
    public static float DEFAULT_WALK_SPEED = 0.2f;

    public RootStatusEffect(int duration)
    {
        super(1, duration);
    }

    @Override
    public String getName()
    {
        return "Rooted";
    }

    @Override
    public ChatColor getColor()
    {
        return ChatColor.DARK_PURPLE;
    }

    @Override
    public void onStart()
    {
        if (this.getEntity().getType() == EntityType.PLAYER)
        {
            Player player = (Player) this.getEntity();
            player.setWalkSpeed(0);
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 128, true, false, false));
        }
        else
        {
            this.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 128, true, false, false));
        }
    }

    @Override
    public void onEnd()
    {
        if (this.getEntity().getType() == EntityType.PLAYER)
        {
            Player player = (Player) this.getEntity();
            player.setWalkSpeed(DEFAULT_WALK_SPEED);
            player.removePotionEffect(PotionEffectType.JUMP);
        }
        else
        {
            this.getEntity().removePotionEffect(PotionEffectType.SLOW);
        }
    }

    @Override
    public void onTick()
    {

    }
}
