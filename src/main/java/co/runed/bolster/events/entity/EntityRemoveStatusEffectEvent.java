package co.runed.bolster.events.entity;

import co.runed.bolster.status.StatusEffect;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.HandlerList;

public final class EntityRemoveStatusEffectEvent extends StatusEffectEvent {
    private static final HandlerList handlers = new HandlerList();

    private final StatusEffect.RemovalCause cause;
    private final Object data;

    public EntityRemoveStatusEffectEvent(LivingEntity entity, StatusEffect statusEffect, StatusEffect.RemovalCause cause, Object data) {
        super(entity, statusEffect);

        this.cause = cause;
        this.data = data;
    }

    public StatusEffect.RemovalCause getCause() {
        return cause;
    }

    public Object getData() {
        return data;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
