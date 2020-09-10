package co.runed.bolster.status;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

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

    @EventHandler
    private void onTakeDamage(EntityDamageEvent event)
    {
        if(this.getEntity().equals(event.getEntity()))
        {
            event.setCancelled(true);
        }
    }
}