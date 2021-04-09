package co.runed.bolster.abilities.base;

import co.runed.bolster.events.EntityTargetedEvent;
import co.runed.bolster.util.BukkitUtil;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A MultiTargetAbility that gets nearby entities
 */
public class AdvancedAOEAbility extends MultiTargetAbility
{
    Target<Location> target;

    private double coneAngle = 0;
    private final double vRadius;
    private final double hRadius;
    private int maxTargets = -1;
    private boolean circleShape = false;

    private final double vRadiusSquared;
    private final double hRadiusSquared;

    public AdvancedAOEAbility(Target<Location> target, double vRadius, double hRadius, double coneAngle, boolean circleShape, int maxTargets)
    {
        super(null);

        this.target = target;
        this.vRadius = vRadius;
        this.hRadius = hRadius;
        this.coneAngle = coneAngle;
        this.circleShape = circleShape;
        this.maxTargets = maxTargets;

        vRadiusSquared = vRadius * vRadius;
        hRadiusSquared = hRadius * hRadius;

        this.setEntityFunction(this::run);
    }

    private Collection<Entity> run(Properties properties)
    {
        int count = 0;

        List<Entity> output = new ArrayList<>();
        Location finalLoc = this.target.get(properties);
        Location location = BukkitUtil.makeFinite(this.target.get(properties));

        List<LivingEntity> entities = location.getWorld().getNearbyEntities(location, hRadius, vRadius, hRadius).stream().filter(e -> e instanceof LivingEntity).map((e) -> (LivingEntity) e).collect(Collectors.toList());

        // check world before distance
        for (LivingEntity entity : entities)
        {
            if (entity.getWorld().equals(finalLoc.getWorld())) continue;
            entities.remove(entity);
        }

        Comparator<LivingEntity> comparator = Comparator.comparingDouble(entity -> entity.getLocation().distanceSquared(finalLoc));
//        if (reverseProximity) comparator = comparator.reversed();
        entities.sort(comparator);

        for (LivingEntity target : entities)
        {
            if (circleShape)
            {
                double hDistance = Math.pow(target.getLocation().getX() - location.getX(), 2) + Math.pow(target.getLocation().getZ() - location.getZ(), 2);
                if (hDistance > hRadiusSquared) continue;

                double vDistance = Math.pow(target.getLocation().getY() - location.getY(), 2);
                if (vDistance > vRadiusSquared) continue;
            }

            if (coneAngle > 0)
            {
                Vector dir = target.getLocation().toVector().subtract(finalLoc.toVector());

                if (Math.toDegrees(Math.abs(dir.angle(finalLoc.getDirection()))) > coneAngle) continue;
            }

            if (target.isDead()) continue;

            EntityTargetedEvent event = new EntityTargetedEvent(target, this.getCaster());
            Bukkit.getPluginManager().callEvent(event);

            if (event.isCancelled()) continue;
//            castSpells(caster, location, target, power);
//            playSpellEffects(EffectPosition.TARGET, target);
//            if (spellSourceInCenter) playSpellEffectsTrail(location, target.getLocation());
//            else if (caster != null) playSpellEffectsTrail(caster.getLocation(), target.getLocation());

            output.add(target);

            count++;

            if (maxTargets > 0 && count >= maxTargets) break;
        }

        return output;
    }
}
