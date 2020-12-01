package co.runed.bolster.status;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collection;

public class InvulnerableStatusEffect extends StatusEffect
{
    public InvulnerableStatusEffect(double duration)
    {
        super(duration);
    }

    @Override
    public String getName()
    {
        return "Invulnerable";
    }

    @Override
    public ChatColor getColor()
    {
        return ChatColor.GOLD;
    }

    @Override
    public Collection<PotionEffectType> getPotionEffects()
    {
        return new ArrayList<>();
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

    @EventHandler(priority = EventPriority.LOWEST)
    private void onTakeDamage(EntityDamageEvent event)
    {
        if (this.getEntity() == null) return;

        if (this.getEntity().equals(event.getEntity()))
        {
            event.setDamage(0);
        }
    }
}
