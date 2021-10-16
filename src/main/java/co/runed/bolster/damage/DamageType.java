package co.runed.bolster.damage;

import org.bukkit.event.entity.EntityDamageEvent;

public enum DamageType {
    PRIMARY(EntityDamageEvent.DamageCause.ENTITY_ATTACK),
    SWEEP_AOE(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK),
    MAGIC(EntityDamageEvent.DamageCause.MAGIC),
    MISC(EntityDamageEvent.DamageCause.CUSTOM);

    private final EntityDamageEvent.DamageCause damageCause;

    DamageType(EntityDamageEvent.DamageCause damageCause) {
        this.damageCause = damageCause;
    }

    public EntityDamageEvent.DamageCause getDamageCause() {
        return damageCause;
    }

    public static DamageType fromCause(EntityDamageEvent.DamageCause damageCause) {
        for (var type : DamageType.values()) {
            if (type.getDamageCause() == damageCause) {
                return type;
            }
        }

        return DamageType.MISC;
    }
}
