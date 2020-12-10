package co.runed.bolster.status;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.Collection;

public class RootStatusEffect extends StatusEffect
{
    public static float DEFAULT_WALK_SPEED = 0.2f;

    public RootStatusEffect(double duration)
    {
        super(duration);
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
    public Collection<PotionEffectType> getPotionEffects()
    {
        return Arrays.asList(PotionEffectType.JUMP, PotionEffectType.SLOW);
    }

    @Override
    public void onStart()
    {
        if (this.getEntity().getType() == EntityType.PLAYER)
        {
            Player player = (Player) this.getEntity();
            player.setWalkSpeed(0);
            this.addPotionEffect(PotionEffectType.JUMP, 128, true, false, false);
        }
        else
        {
            this.addPotionEffect(PotionEffectType.SLOW, 128, true, false, false);
        }
    }

    @Override
    public void onEnd()
    {
        if (this.getEntity().getType() == EntityType.PLAYER)
        {
            Player player = (Player) this.getEntity();
            player.setWalkSpeed(DEFAULT_WALK_SPEED);
        }
    }

    @Override
    public void onTick()
    {

    }
}
