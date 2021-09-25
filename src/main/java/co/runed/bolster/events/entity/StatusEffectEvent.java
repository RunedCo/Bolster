package co.runed.bolster.events.entity;

import co.runed.bolster.status.StatusEffect;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public abstract class StatusEffectEvent extends Event implements Cancellable {
    private final LivingEntity entity;
    private final StatusEffect statusEffect;

    private boolean cancelled = false;

    public StatusEffectEvent(LivingEntity entity, StatusEffect statusEffect) {
        this.entity = entity;
        this.statusEffect = statusEffect;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public StatusEffect getStatusEffect() {
        return statusEffect;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }
}
