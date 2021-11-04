package co.runed.bolster.damage;

import co.runed.bolster.entity.BolsterEntity;
import co.runed.bolster.util.Owned;
import co.runed.bolster.v1_16_R3.CraftUtil;
import co.runed.bolster.wip.DamageListener;
import co.runed.dayroom.gson.JsonExclude;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.UUID;

public class DamageInfo {
    protected UUID damageId = UUID.randomUUID();
    protected double damage;
    protected DamageType damageType = DamageType.PRIMARY;
    @JsonExclude
    protected LivingEntity attacker;
    @JsonExclude
    protected DamageSource damageSource;
    protected int noDamageTicks = -1;

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

        if (attacker == null) {
            if (damageSource instanceof BolsterEntity bolsterEntity) {
                return withAttacker(bolsterEntity.getEntity());
            }

            if (damageSource instanceof Owned owned) {
                return withAttacker(owned.getOwner());
            }
        }

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
        var info = new EventDamageInfo(event.getFinalDamage());

        if (event instanceof EntityDamageByEntityEvent entityEvent) {
            if (entityEvent.getDamager() instanceof LivingEntity damager) {
                info.withAttacker(damager)
                        .withSource(BolsterEntity.from(damager));
            }
        }

        info.withType(DamageType.fromCause(event.getCause()));

        return info;
    }

    public static class EventDamageInfo extends DamageInfo {
        public EventDamageInfo(double damage) {
            super(damage);
        }

        @Override
        public DamageInfo clone() {
            return new EventDamageInfo(damage)
                    .withAttacker(attacker)
                    .withSource(damageSource)
                    .withType(damageType)
                    .setNoDamageTicks(noDamageTicks);
        }
    }
}
