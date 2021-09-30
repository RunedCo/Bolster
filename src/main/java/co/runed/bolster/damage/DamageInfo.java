package co.runed.bolster.damage;

import co.runed.bolster.entity.BolsterEntity;
import co.runed.bolster.v1_16_R3.CraftUtil;
import co.runed.bolster.wip.DamageListener;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.UUID;

public class DamageInfo {
    private UUID damageId = UUID.randomUUID();
    private double damage;
    private DamageType damageType = DamageType.PRIMARY;
    private LivingEntity attacker;
    private DamageSource damageSource;
    private int noDamageTicks = -1;

    public DamageInfo(double damage) {
        this.damage = damage;
    }

    public UUID getId() {
        return damageId;
    }

    public double getDamage() {
        return damage;
    }

    public DamageInfo withType(DamageType type) {
        this.damageType = type;

        return this;
    }

    public DamageType getDamageType() {
        return damageType;
    }

    public DamageInfo withAttacker(LivingEntity attacker) {
        this.attacker = attacker;

        return this;
    }

    public LivingEntity getAttacker() {
        return attacker;
    }

    public DamageInfo withSource(DamageSource damageSource) {
        this.damageSource = damageSource;

        return this;
    }

    public DamageSource getDamageSource() {
        return damageSource;
    }

    public DamageInfo setNoDamageTicks(int noDamageTicks) {
        this.noDamageTicks = noDamageTicks;

        return this;
    }

    public int getNoDamageTicks() {
        return noDamageTicks;
    }

    @Override
    public DamageInfo clone() {
        return new DamageInfo(damage)
                .withAttacker(attacker)
                .withSource(damageSource)
                .withType(damageType)
                .setNoDamageTicks(noDamageTicks);
    }

    public DamageInfo apply(Entity target) {
        if (target == null) return this;

        DamageListener.getInstance().queueDamageInfo(target, this);

        CraftUtil.damageEntity(target, damage, attacker, damageType.getDamageCause());

        if (target instanceof LivingEntity le && noDamageTicks > -1) {
            le.setNoDamageTicks(noDamageTicks);
        }

        return this;
    }

    public static DamageInfo fromEvent(EntityDamageEvent event) {
        var info = new DamageInfo(event.getFinalDamage());

        if (event instanceof EntityDamageByEntityEvent entityEvent) {
            if (entityEvent.getDamager() instanceof LivingEntity damager) {
                info.withAttacker(damager)
                        .withSource(BolsterEntity.from(damager));
            }
        }

        info.withType(DamageType.fromCause(event.getCause()));

        return info;
    }
}
