package co.runed.bolster.status;

import co.runed.bolster.events.entity.EntityTargetedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

public class UntargetableStatusEffect extends InvulnerableStatusEffect {
    public UntargetableStatusEffect(double duration) {
        super(duration);
    }

    @EventHandler
    private void onTargeted(EntityTargetedEvent event) {
        if (!event.getEntity().getUniqueId().equals(this.getEntity().getUniqueId())) return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onEntityTarget(EntityTargetLivingEntityEvent event) {
        if (event.getTarget() == null) return;
        if (!event.getTarget().getUniqueId().equals(this.getEntity().getUniqueId())) return;

        event.setCancelled(true);
    }
}
