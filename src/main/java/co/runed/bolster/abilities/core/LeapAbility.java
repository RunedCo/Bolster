package co.runed.bolster.abilities.core;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.Ability;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class LeapAbility extends Ability
{
    float upwardVelocity;
    float forwardVelocity;
    boolean ignoreFallDamage = true;

    boolean ignoreNext = false;
    boolean delayedIgnoreTriggered = false;
    boolean touchedGround = false;

    public LeapAbility(float upwardVelocity, float forwardVelocity)
    {
        this(upwardVelocity, forwardVelocity, true);
    }

    public LeapAbility(float upwardVelocity, float forwardVelocity, boolean ignoreFallDamage)
    {
        this.upwardVelocity = upwardVelocity;
        this.forwardVelocity = forwardVelocity;
        this.ignoreFallDamage = ignoreFallDamage;
    }

    @Override
    public void onActivate(Properties properties)
    {
        Vector v = this.getCaster().getLocation().getDirection();
        v.setY(0).normalize().multiply(forwardVelocity).setY(upwardVelocity);

        this.touchedGround = false;

        if (this.ignoreFallDamage) this.ignoreNext = true;

        this.getCaster().setVelocity(v);
    }

    @Override
    public boolean isInProgress()
    {
        return !this.touchedGround;
    }

    public void setIgnoreFallDamage(boolean ignoreFallDamage)
    {
        this.ignoreFallDamage = ignoreFallDamage;
    }

    public boolean getIgnoreFallDamage()
    {
        return ignoreFallDamage;
    }

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event)
    {
        if (this.delayedIgnoreTriggered) return;
        if (event.getPlayer() != this.getCaster()) return;
        if (!event.getPlayer().isOnGround()) return;

        this.delayedIgnoreTriggered = true;

        Bukkit.getScheduler().scheduleSyncDelayedTask(Bolster.getInstance(), () -> {
            this.ignoreNext = false;
            this.delayedIgnoreTriggered = false;
            this.touchedGround = true;
        }, 10L);
    }

    @EventHandler
    private void onNextFallDamage(EntityDamageEvent event)
    {
        if (!ignoreNext) return;
        if (event.getEntity() != this.getCaster()) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;

        this.ignoreNext = false;

        event.setCancelled(true);
    }
}
