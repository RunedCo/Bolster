package co.runed.bolster.util;

import co.runed.bolster.Bolster;
import co.runed.bolster.util.task.RepeatingTask;
import co.runed.bolster.util.task.Task;
import co.runed.bolster.v1_16_R3.CraftUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class Leap implements Listener {
    private Vector velocity;
    private double upwardVelocity;
    private double forwardVelocity;
    private boolean started = false;
    private boolean ignoreFallDamage = true;
    private boolean touchedGround = false;
    private boolean delayedIgnoreTriggered = false;
    private LivingEntity entity = null;
    private Runnable onLand = null;
    private boolean playAnimation = false;

    private Task checkTask;

    public Leap(Vector velocity) {
        this(0, 0);

        this.velocity = velocity;
    }

    public Leap(double upwardVelocity, double forwardVelocity) {
        this.upwardVelocity = upwardVelocity;
        this.forwardVelocity = forwardVelocity;

        Bukkit.getPluginManager().registerEvents(this, Bolster.getInstance());
    }

    public Leap ignoreFallDamage(boolean ignoreFallDamage) {
        this.ignoreFallDamage = ignoreFallDamage;

        return this;
    }

    public Leap onLand(Runnable function) {
        this.onLand = function;

        return this;
    }

    public Leap playAnimation(boolean playAnimation) {
        this.playAnimation = playAnimation;

        return this;
    }

    public Leap apply(LivingEntity entity) {
        var velocity = entity.getLocation().getDirection().clone();

        velocity.setY(0).normalize().multiply(forwardVelocity).setY(upwardVelocity);

        if (this.velocity != null) velocity = this.velocity;

        entity.setVelocity(velocity);

        if (!(entity instanceof Player) && checkTask == null) {
            checkTask = new RepeatingTask(5L)
                    .delay(10L)
                    .run(() -> {
                        if (entity.isOnGround()) stop();
                    });
        }

        started = true;
        this.entity = entity;

        if (entity instanceof Player player && playAnimation) {
            CraftUtil.playRiptideAnimation(player, 10);
        }

        return this;
    }

    private void stop() {
        if (checkTask != null) checkTask.cancel();

        entity = null;
        started = false;

        if (onLand != null) onLand.run();

        HandlerList.unregisterAll(this);
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onPlayerMove(PlayerMoveEvent event) {
        if (delayedIgnoreTriggered) return;
        if (entity == null) return;
        if (!event.getPlayer().getUniqueId().equals(entity.getUniqueId())) return;
        if (!event.getPlayer().isOnGround()) return;

        delayedIgnoreTriggered = true;

        Bukkit.getScheduler().scheduleSyncDelayedTask(Bolster.getInstance(), () -> {
            delayedIgnoreTriggered = false;
            touchedGround = true;

            stop();
        }, 10L);
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onNextFallDamage(EntityDamageEvent event) {
        if (!started) return;
        if (entity == null) return;
        if (!event.getEntity().getUniqueId().equals(entity.getUniqueId())) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;

        stop();

        event.setCancelled(true);
    }
}
