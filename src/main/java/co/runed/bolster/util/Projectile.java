package co.runed.bolster.util;

import co.runed.bolster.Bolster;
import co.runed.bolster.damage.DamageInfo;
import co.runed.bolster.fx.particles.ParticleSet;
import co.runed.bolster.fx.particles.ParticleType;
import co.runed.bolster.fx.particles.effects.LineParticleEffect;
import co.runed.bolster.game.Team;
import co.runed.bolster.managers.EntityManager;
import co.runed.bolster.util.task.RepeatingTask;
import co.runed.bolster.util.task.Task;
import co.runed.bolster.v1_16_R3.CraftUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Projectile implements Listener {
    private LivingEntity owner;

    private EntityType entityType = null;
    private Entity entity;
    private boolean entityVisible = true;
    private World world;
    private Vector location = null;
    private Vector direction = null;
    private double speed = 1.0;
    private ParticleType particles;

    private double hitBoxWidth = 0;
    private double hitBoxHeight = 0;

    private Duration duration;

    private int pierceCount = 0;
    private int numHits = 0;
    private DamageInfo damage = null;

    private HitMode hitMode = HitMode.ALL;
    private Collection<UUID> ignoredEntities = new ArrayList<>();

    private Collection<Consumer<Projectile>> onStart = new ArrayList<>();
    private Collection<Consumer<Projectile>> onTick = new ArrayList<>();
    private Collection<BiConsumer<Projectile, Block>> onHitBlock = new ArrayList<>();
    private Collection<BiConsumer<Projectile, LivingEntity>> onHit = new ArrayList<>();
    private Collection<Consumer<Projectile>> onFinish = new ArrayList<>();
    private boolean removeOnFinish = true;

    // run instance vars
    private Task tickTask;
    private boolean launched = false;
    private Location startPos;
    private Location previousPos;
    private Location currentPos;
    private Vector currentDir;
    private Block hitBlock;

    public Projectile() {
        this(EntityType.SNOWBALL);

        this.visible(false);
        this.hitbox(1, 1);
    }

    public Projectile(EntityType entityType) {
        this((Entity) null);

        this.entityType = entityType;
    }

    public Projectile(Entity entity) {
        this.entity = entity;

        if (entity != null) {
            location(entity.getLocation());
        }

        Bukkit.getPluginManager().registerEvents(this, Bolster.getInstance());
    }

    // Owner
    public Projectile owner(LivingEntity owner) {
        this.owner = owner;
        ignore(owner);

        if (location == null) {
            location(owner.getEyeLocation());
        }

        return this;
    }

    // Position, Direction, Velocity
    public Projectile location(Location location) {
        this.world = location.getWorld();
        this.location = location.toVector();

        if (direction == null) {
            direction = location.getDirection();
        }

        return this;
    }

    public Projectile direction(Vector direction) {
        this.direction = direction;

        return this;
    }

    public Projectile velocity(Vector velocity) {
        var speed = velocity.length();

        direction(velocity.clone().divide(new Vector(speed, speed, speed)).normalize());

//        System.out.println("spd: " + speed + " vel: " + velocity + " dir: " + direction);

        if (this.speed <= 1) {
            this.speed = speed;
        }

        return this;
    }

    public Projectile speed(double speed) {
        this.speed = speed;

        return this;
    }

    public Projectile hitbox(double width, double height) {
        this.hitBoxWidth = width;
        this.hitBoxHeight = height;

        return this;
    }

    public Projectile damage(DamageInfo damage) {
        this.damage = damage;

        return this;
    }

    // Ignore collision with specific entities
    public Projectile hitMode(HitMode hitMode) {
        this.hitMode = hitMode;

        return this;
    }

    public Projectile ignore(Team team) {
        return this.ignore(team.getMembers());
    }

    public Projectile ignore(LivingEntity entity) {
        return this.ignore(Collections.singletonList(entity));
    }

    public Projectile ignore(Collection<LivingEntity> entities) {
        ignoredEntities.addAll(entities.stream().map(Entity::getUniqueId).collect(Collectors.toList()));

        return this;
    }

    public Projectile visible(boolean visible) {
        this.entityVisible = visible;

        return this;
    }

    // Duration
    public Projectile duration(Duration duration) {
        this.duration = duration;

        return this;
    }

    public Projectile particles(ParticleType particles) {
        this.particles = particles;

        return this;
    }

    // Lifecycle functions
    public Projectile onStart(Consumer<Projectile> function) {
        this.onStart.add(function);

        return this;
    }

    public Projectile onTick(Consumer<Projectile> function) {
        this.onTick.add(function);

        return this;
    }

    public Projectile onHit(BiConsumer<Projectile, LivingEntity> function) {
        this.onHit.add(function);

        return this;
    }

    public Projectile onHitBlock(BiConsumer<Projectile, Block> function) {
        this.onHitBlock.add(function);

        return this;
    }

    public Projectile onFinish(Consumer<Projectile> function) {
        this.onFinish.add(function);

        return this;
    }

    public Projectile removeOnFinish(boolean remove) {
        this.removeOnFinish = remove;

        return this;
    }

    public LivingEntity getOwner() {
        return owner;
    }

    public Entity getEntity() {
        return entity;
    }

    public Location getLocation() {
        return currentPos;
    }

    public Location getPreviousLocation() {
        return previousPos;
    }

    public Location getStartLocation() {
        return startPos;
    }

    // Actually launch the projectile
    public Projectile run() {
        // Check if already launched and if so don't run again
        if (launched) return this;
        launched = true;

        // Set projectile location + direction

//        var velocity = this.velocity.clone();
        var velocity = direction.clone().normalize().multiply(speed);
        var velocitySet = false;

        // Spawn entity if not consuming existing entity
        if (entity == null && entityType != null) {
            var entityClass = entityType.getEntityClass();

            if (owner != null && entityClass != null && org.bukkit.entity.Projectile.class.isAssignableFrom(entityClass)) {
                entity = owner.launchProjectile((Class<? extends org.bukkit.entity.Projectile>) entityClass, velocity);
                velocitySet = true;
            }
            else {
                entity = world.spawnEntity(startPos, entityType);
            }
        }

        if (entity == null) {
            Bolster.debug("NULL ENTITY IN PROJECTILE! ABORTING!");
            return this;
        }

        // Set projectile velocity
        if (!velocitySet) entity.setVelocity(velocity);

        startPos = entity.getLocation().clone();
        previousPos = startPos.clone();
        currentPos = previousPos.clone();
        currentDir = direction.clone();

        if (owner != null && entity instanceof org.bukkit.entity.Projectile proj) {
            proj.setShooter(owner);
        }

        // Adds ignored entities to projectile collision
        if (entity instanceof LivingEntity le) {
            le.setCollidable(false);
//            le.getCollidableExemptions().addAll(ignoredEntities);
        }

        // Set default hitbox sized
        if (hitBoxWidth <= 0) {
            hitBoxWidth = entity.getWidth();
        }

        if (hitBoxHeight <= 0) {
            hitBoxHeight = entity.getHeight();
        }

        // If projectile is item, don't allow picking up
        if (entity instanceof Item item) {
            item.setPickupDelay(1000000);
        }

        // Hide entity if not visible
        if (!entityVisible) {
            for (var player : Bukkit.getOnlinePlayers()) {
                CraftUtil.hideEntity(player, entity);
            }
        }

        // Trigger onStart
        onStart.forEach(func -> func.accept(this));

        // Start ticking function (for particles or whatever proj needs)
        tickTask = new RepeatingTask(1L)
                .duration(duration)
                .delay(1L)
                .run(this::doTick);

        return this;
    }

    private void doTick() {
        if (!launched) return;

        previousPos = currentPos.clone();

        currentPos = entity.getBoundingBox().getCenter().toLocation(world);

//        owner.sendMessage("prev: " + previousPos.toVector() + " curr: " + currentPos.toVector());

        if (currentPos.distanceSquared(previousPos) > 0.5) currentDir = currentPos.toVector().subtract(previousPos.toVector()).normalize();

//        owner.sendMessage(ChatColor.AQUA + "dir: " + currentDir + " dis: " + currentPos.distanceSquared(previousPos));

        doParticles();

        onTick.forEach(func -> func.accept(this));

//        if (entity instanceof Arrow arrow && arrow.isInBlock()) {
//            hitBlock = arrow.getAttachedBlock();
//        }

        if (entity.isDead() || !entity.isDead() && entity.isOnGround() || hitBlock != null) {

            if (hitBlock == null) {
                hitBlock = world.getBlockAt(currentPos);
            }

            // DO BLOCK HIT
            doBlockHit(hitBlock);

            destroy();
            return;
        }

        var wRadius = hitBoxWidth / 2;
        var hRadius = hitBoxHeight / 2;

        var canHit = entity.getNearbyEntities(wRadius, hRadius, wRadius);
        canHit.removeIf(e -> !canHit(e));

        if (canHit.size() <= 0) {
            return;
        }

        for (var e : canHit) {
            if (e instanceof LivingEntity target) {
                doHit(target);
                break;
            }
        }
    }

    private void doParticles() {
        if (particles == null || currentDir == null) return;

        var particleInfo = ParticleSet.getActive(owner).get(particles);
        var effect = new LineParticleEffect(currentPos, previousPos, (int) ((speed + 1) * 2));

        effect.run(world, particleInfo);

//        var steps = ((speed + 1) * 2);
//
//        for (var i = 0; i < steps; i++) {
//            particleInfo.spawnParticle(world, previousPos.clone().add(currentDir.clone().multiply(i / 3d)), 1, 0, 0, 0, 1);
//        }
    }

    private void doHit(LivingEntity target) {
        var success = target != null;
        if (success) {
            numHits++;

            // TODO: NO DAMAGE TICKS
//            if (target.getNoDamageTicks() > 1) {
//                target.setNoDamageTicks(0);
//            }

            onHit.forEach(func -> func.accept(this, target));

            if (damage != null) {
                damage.apply(target);
            }

//            var knockback = getKnockback();
//            var upwardKnockback = getUpwardKnockback();
//
//            var velocity = projectile.getVelocity();


            // TODO: KB
//            if (upwardKnockback != 0) {
//                VelocityUtil.addKnockback(firer, target, knockback, 0.5);
//            }
//            else {
//                velocity = velocity.normalize().multiply(knockback);
//                target.setVelocity(velocity);
//            }
        }
//        else {
//            doBlockHit();
//        }

        if (pierceCount <= 0 || numHits >= pierceCount + 1) {
            destroy();
        }
    }

    private void doBlockHit(Block block) {
        onHitBlock.forEach(func -> func.accept(this, block));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onProjectileHit(ProjectileHitEvent event) {
        var proj = event.getEntity();

        if (proj != entity) return;

        if (event.getHitBlock() != null) {
            // DO BLOCK HIT
//            doBlockHit(event.getHitBlock());

            hitBlock = event.getHitBlock();
        }

        if (event.isCancelled()) return;

        if (event.getHitEntity() != null) {
            if (event.getHitEntity() instanceof LivingEntity le) {
                doHit(le);
            }

            if (numHits < pierceCount + 1) {
                event.setCancelled(true);
            }
        }
    }

    private boolean canHit(Entity e) {
        if (ignoredEntities.contains(e.getUniqueId())) return false;
        if (entity.getPassengers().contains(e)) return false;
        // NOTE: metadata might be a better solution here, but this should work for pretty much all armor stands at least
        if (!e.hasGravity()) return false;

        var isAllied = EntityManager.getInstance().areEntitiesAllied(owner, entity);

        if (owner != null && hitMode == HitMode.ONLY_ALLIES && !isAllied) return false;
        if (owner != null && hitMode == HitMode.ONLY_NON_ALLIES && isAllied) return false;

        return true;
    }

    public void destroy() {
        destroy(removeOnFinish);
    }

    public void destroy(boolean remove) {
        if (launched) {
            HandlerList.unregisterAll(this);

            if (tickTask != null) tickTask.cancel();
            onFinish.forEach(func -> func.accept(this));
        }

        launched = false;

        if (remove) {
            for (var passenger : entity.getPassengers()) {
                passenger.remove();
            }

            entity.remove();
        }
    }

    public enum HitMode {
        ALL,
        ONLY_ALLIES,
        ONLY_NON_ALLIES
    }
}
