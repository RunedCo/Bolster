package co.runed.bolster.status;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class InvulnerableStatusEffect extends StatusEffect
{
    public InvulnerableStatusEffect(int duration)
    {
        super(1, duration);
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
