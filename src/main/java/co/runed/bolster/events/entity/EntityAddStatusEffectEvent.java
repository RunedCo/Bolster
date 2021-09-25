package co.runed.bolster.events.entity;

import co.runed.bolster.status.StatusEffect;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.HandlerList;

public final class EntityAddStatusEffectEvent extends StatusEffectEvent {
    private static final HandlerList handlers = new HandlerList();

    public EntityAddStatusEffectEvent(LivingEntity entity, StatusEffect statusEffect) {
        super(entity, statusEffect);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}

