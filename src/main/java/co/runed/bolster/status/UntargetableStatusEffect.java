package co.runed.bolster.status;

import co.runed.bolster.events.EntityTargetedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

public class UntargetableStatusEffect extends InvulnerableStatusEffect
{
    public UntargetableStatusEffect(double duration)
    {
        super(duration);
    }

    @Override
    public String getName()
    {
        return "Untargetable";
    }

    @EventHandler
    private void onTargeted(EntityTargetedEvent event)
    {
        if (!event.getEntity().equals(this.getEntity())) return;

        event.setCancelled(true);
    }

    @EventHandler
    private void onTargetedByEntity(EntityTargetLivingEntityEvent event)
    {
        if (event.getTarget() == null) return;
        if (!event.getTarget().equals(this.getEntity())) return;

        event.setCancelled(true);
    }
}
