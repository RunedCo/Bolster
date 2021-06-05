package co.runed.bolster.events;

import co.runed.bolster.managers.CooldownManager;
import co.runed.bolster.util.cooldown.ICooldownSource;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.time.Instant;

public class EntitySetCooldownEvent extends Event// implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    protected boolean cancelled;

    LivingEntity entity;
    Instant castTime;
    String cooldownId;
    int slot;
    double cooldown;
    boolean isGlobal;

    public EntitySetCooldownEvent(LivingEntity entity, Instant castTime, String cooldownId, int slot, double cooldown, boolean isGlobal)
    {
        this.cancelled = false;

        this.entity = entity;
        this.castTime = castTime;
        this.cooldownId = cooldownId;
        this.slot = slot;
        this.cooldown = cooldown;
        this.isGlobal = isGlobal;
    }

    public LivingEntity getEntity()
    {
        return entity;
    }

    public Instant getCastTime()
    {
        return castTime;
    }

    public String getCooldownId()
    {
        return cooldownId;
    }

    public double getCooldown()
    {
        return cooldown;
    }

    public int getSlot()
    {
        return slot;
    }

    public boolean isGlobal()
    {
        return isGlobal;
    }

    public CooldownManager.CooldownData getCooldownData()
    {
        return new CooldownManager.CooldownData(entity, cooldownId, slot, castTime, (long) (cooldown * 1000), isGlobal);
    }

//    public boolean isCancelled()
//    {
//        return this.cancelled;
//    }
//
//    public void setCancelled(boolean cancel)
//    {
//        this.cancelled = cancel;
//    }

    public HandlerList getHandlers()
    {
        return handlers;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}
