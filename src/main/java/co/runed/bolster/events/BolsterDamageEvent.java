package co.runed.bolster.events;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;

public class BolsterDamageEvent extends EntityDamageEvent
{
    public BolsterDamageEvent(Entity damagee, DamageCause cause, double damage)
    {
        super(damagee, cause, damage);
    }
}
