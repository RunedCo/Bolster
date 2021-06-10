package co.runed.bolster.events.entity;

import co.runed.bolster.abilities.AbilityProvider;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.events.AbilityTriggerEvent;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.entity.LivingEntity;
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
