package co.runed.bolster.damage;

import co.runed.bolster.entity.BolsterEntity;
import co.runed.bolster.v1_16_R3.CraftUtil;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageInfo {
    double damage;
    DamageType damageType = DamageType.PRIMARY;
    LivingEntity attacker;
    DamageSource damageSource;
    int noDamageTicks = -1;

    public DamageInfo(double damage) {
        this.damage = damage;
    }

    public DamageInfo withType(DamageType type) {
        this.damageType = type;

        return this;
    }

    public DamageInfo withAttacker(LivingEntity attacker) {
        this.attacker = attacker;

        return this;
    }

    public DamageInfo withSource(DamageSource damageSource) {
        this.damageSource = damageSource;

        return this;
    }

    public DamageInfo setNoDamageTicks(int noDamageTicks) {
        this.noDamageTicks = noDamageTicks;

        return this;
    }

    public DamageInfo apply(LivingEntity target) {
        if (target == null) return this;

        CraftUtil.damageEntity(target, damage, attacker, damageType.getDamageCause());

        if (noDamageTicks > -1) {
            target.setNoDamageTicks(noDamageTicks);
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
