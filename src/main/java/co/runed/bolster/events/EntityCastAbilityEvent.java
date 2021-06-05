package co.runed.bolster.events;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.HandlerList;

public final class EntityCastAbilityEvent extends AbilityTriggerEvent
{
    private static final HandlerList handlers = new HandlerList();

    private final Ability ability;

    public EntityCastAbilityEvent(LivingEntity entity, Ability ability, AbilityTrigger trigger, Properties properties)
    {
        super(entity, ability.getAbilityProvider(), trigger, properties);

        this.ability = ability;
    }

    public Ability getAbility()
    {
        return ability;
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

