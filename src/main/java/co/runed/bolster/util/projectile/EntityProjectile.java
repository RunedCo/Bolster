package co.runed.bolster.util.projectile;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;

public class EntityProjectile extends BukkitRunnable
{

    protected Plugin plugin;
    protected LivingEntity firer;
    protected boolean expAdd = false;
    protected Entity projectile;
    private Location overridePosition;

    private double duration;
    private boolean lastsOnGround = false;
    private boolean fireOpposite = false;
    private boolean clearOnFinish = true;
    private boolean direct = false;
    private boolean fired;
    private boolean piercing;

    private double damage = 0;
    private double speed = 0;
    private double knockback = 0;
    private double upwardKnockback = 0;
    private double hitboxSize = 0;
    private double variation = 0;

    private Random random = new Random();

    public EntityProjectile(Plugin plugin, LivingEntity firer, Entity projectile)
    {
        this.plugin = plugin;
        this.firer = firer;
        this.projectile = projectile;
    }

    public double getRandomVariation()
    {
        double variation = getVariation();
        double randomAngle = Math.random() * variation / 2;

        if (random.nextBoolean())
        {
            randomAngle *= -1;
        }

        return randomAngle * Math.PI / 180;
    }

    public void launch()
    {
        if (duration > 0)
        {
            Bukkit.getScheduler().runTaskLater(plugin, projectile::remove, (long) (duration * 20));
        }

        if (fired) return;

        if (getOverridePosition() == null)
        {
            setOverridePosition(firer.getEyeLocation());
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> projectile.teleport(getOverridePosition()), 2L);

        if (projectile instanceof Item)
        {
            Item item = (Item) projectile;
            item.setPickupDelay(1000000);
        }

        double magnitude = getSpeed();
        Vector direction = firer.getLocation().getDirection();

        if (getFireOpposite())
        {
            direction.multiply(-1);
        }

        direction.rotateAroundX(getRandomVariation());
        direction.rotateAroundY(getRandomVariation());
        direction.rotateAroundZ(getRandomVariation());

        if (direct)
        {
            projectile.setVelocity(direction.multiply(magnitude).setY(0).normalize());
        }
        else
        {
            projectile.setVelocity(direction.multiply(magnitude));
        }

        this.runTaskTimer(plugin, 0L, 1L);
        fired = true;
    }

    @Override
    public void run()
    {
        if (projectile.isDead() || !projectile.isDead() && projectile.isOnGround())
        {
            onHit(null);
            this.cancel();
            return;
        }

        double hitboxRange = getHitboxSize();

        List<Entity> canHit = projectile.getNearbyEntities(hitboxRange, hitboxRange, hitboxRange);
        canHit.remove(projectile);
        canHit.remove(firer);

        if (canHit.size() <= 0) return;

        for (Entity entity : canHit)
        {
            if (!(entity instanceof LivingEntity)) continue;
            if (entity.getUniqueId().equals(projectile.getUniqueId())) continue;

            LivingEntity target = (LivingEntity) canHit.get(0);
            onHit(target);
            break;
        }
    }

    public boolean onHit(LivingEntity target)
    {
        boolean hitEntity = target != null;

        if (hitEntity)
        {
            if (target.getNoDamageTicks() > 1)
            {
                target.setNoDamageTicks(0);
            }

            double damage = getDamage();
//            DamageUtil.dealDamage(firer, target, damage, true, expAdd);

            double knockbackResistance = target.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).getValue();
            double knockback = getKnockback();
            double upwardKnockback = getUpwardKnockback();
            Vector velocity = projectile.getVelocity().clone();

            velocity = velocity.normalize().multiply(knockback);
            if (upwardKnockback > 0) velocity = velocity.setY(upwardKnockback);

            velocity = velocity.multiply(-knockbackResistance);

            target.setVelocity(velocity);
        }
        else
        {
            if (!lastsOnGround)
            {
                projectile.remove();
            }

            onBlockHit();
        }

        if (!piercing)
        {
            clearProjectile();
        }

        return hitEntity;
    }

    public void onBlockHit()
    {

    }

    public boolean clearProjectile()
    {
        if (getClearOnFinish())
        {
            projectile.remove();
            return true;
        }

        return false;
    }

    public void setOverridePosition(Location overridePosition)
    {
        this.overridePosition = overridePosition;
    }

    public Location getOverridePosition()
    {
        return overridePosition;
    }

    public void setFireOpposite(boolean fireOpposite)
    {
        this.fireOpposite = fireOpposite;
    }

    public boolean getFireOpposite()
    {
        return fireOpposite;
    }

    public void setClearOnFinish(boolean clearOnFinish)
    {
        this.clearOnFinish = clearOnFinish;
    }

    public boolean getClearOnFinish()
    {
        return clearOnFinish;
    }

    public void setDamage(double damage)
    {
        this.damage = damage;
    }

    public double getDamage()
    {
        return damage;
    }

    public void setSpeed(double speed)
    {
        this.speed = speed;
    }

    public double getSpeed()
    {
        return speed;
    }

    public void setKnockback(double knockback)
    {
        this.knockback = knockback;
    }

    public double getKnockback()
    {
        return knockback;
    }

    public void setUpwardKnockback(double upwardKnockback)
    {
        this.upwardKnockback = upwardKnockback;
    }

    public double getUpwardKnockback()
    {
        return upwardKnockback;
    }

    public void setHitboxSize(double hitboxSize)
    {
        this.hitboxSize = hitboxSize;
    }

    public double getHitboxSize()
    {
        return hitboxSize;
    }

    public void setVariation(double variation)
    {
        this.variation = variation;
    }

    public double getVariation()
    {
        return variation;
    }

    public boolean getExpAdd()
    {
        return expAdd;
    }

    public void setExpAdd(boolean expAdd1)
    {
        expAdd = expAdd1;
    }

    public boolean getPiercing()
    {
        return piercing;
    }

    public void setPiercing(boolean piercing)
    {
        this.piercing = piercing;
    }

    public boolean getLastsOnGround()
    {
        return lastsOnGround;
    }

    public void setLastsOnGround(boolean lastsOnGround1)
    {
        lastsOnGround = lastsOnGround1;
    }

    public double getDuration()
    {
        return duration;
    }

    public void setDuration(double time1)
    {
        duration = time1;
    }

    public void setDirect(boolean direct1)
    {
        direct = direct1;
    }
}