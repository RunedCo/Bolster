package co.runed.bolster.status;

import co.runed.bolster.events.EntityTargetedEvent;
import co.runed.bolster.status.StatusEffect;
import co.runed.bolster.wip.CombatTracker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.potion.PotionEffectType;

public class StealthStatusEffect extends StatusEffect
{
    public StealthStatusEffect(double duration)
    {
        super(duration);
    }

    @Override
    public String getName()
    {
        return "Stealthed";
    }

    @Override
    public void onStart()
    {
        CombatTracker.clearAggro(this.getEntity());

        this.addPotionEffect(PotionEffectType.INVISIBILITY, 0, false, true, false);
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
    private void onTargeted(EntityTargetedEvent event)
    {
        if (!event.getEntity().getUniqueId().equals(this.getEntity().getUniqueId())) return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onEntityTarget(EntityTargetLivingEntityEvent event)
    {
        if (event.getTarget() == null) return;
        if (!event.getTarget().getUniqueId().equals(this.getEntity().getUniqueId())) return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onEntityTakeDamage(EntityDamageEvent event)
    {
        if (!event.getEntity().getUniqueId().equals(this.getEntity().getUniqueId())) return;

        this.clear(RemovalCause.INTERNAL, StealthClearReason.TAKE_DAMAGE);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onEntityDealDamage(EntityDamageByEntityEvent event)
    {
        if (!event.getDamager().getUniqueId().equals(this.getEntity().getUniqueId())) return;

        this.clear(RemovalCause.INTERNAL, StealthClearReason.DEAL_DAMAGE);
    }

    public enum StealthClearReason
    {
        DEAL_DAMAGE,
        TAKE_DAMAGE
    }
}
