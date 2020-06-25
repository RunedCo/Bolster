package co.runed.bolster.abilities.events;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.properties.Properties;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class EntityCastAbilityEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private final LivingEntity entity;
    private final Ability ability;
    private final AbilityTrigger trigger;
    private final Properties properties;

    public EntityCastAbilityEvent(LivingEntity entity, Ability ability, AbilityTrigger trigger, Properties properties) {
        this.entity = entity;
        this.ability = ability;
        this.trigger = trigger;
        this.properties = properties;
    }

    public Ability getAbility() {
        return ability;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public AbilityTrigger getTrigger() {
        return trigger;
    }

    public Properties getProperties() {
        return properties;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
