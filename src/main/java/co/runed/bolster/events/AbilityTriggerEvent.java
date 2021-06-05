package co.runed.bolster.events;

import co.runed.bolster.abilities.AbilityProvider;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class AbilityTriggerEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private final LivingEntity entity;
    private final AbilityProvider abilityProvider;
    private final AbilityTrigger trigger;
    private final Properties properties;

    public AbilityTriggerEvent(LivingEntity entity, AbilityProvider abilityProvider, AbilityTrigger trigger, Properties properties)
    {
        this.entity = entity;
        this.abilityProvider = abilityProvider;
        this.trigger = trigger;
        this.properties = properties;
    }

    public AbilityProvider getAbilityProvider()
    {
        return abilityProvider;
    }

    public LivingEntity getEntity()
    {
        return entity;
    }

    public AbilityTrigger getTrigger()
    {
        return trigger;
    }

    public Properties getProperties()
    {
        return properties;
    }

    @Override
    public boolean isCancelled()
    {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled)
    {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}