package co.runed.bolster.events;

import co.runed.bolster.abilities.AbilityProvider;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class EntityPreCastAbilityEvent extends AbilityTriggerEvent
{
    private static final HandlerList handlers = new HandlerList();

    public EntityPreCastAbilityEvent(LivingEntity entity, AbilityProvider abilityProvider, AbilityTrigger trigger, Properties properties)
    {
        super(entity, abilityProvider, trigger, properties);
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
