package co.runed.bolster.damage;

import org.bukkit.event.entity.EntityDamageEvent;

public enum DamageType {
    PRIMARY(EntityDamageEvent.DamageCause.ENTITY_ATTACK),
    SECONDARY(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK);

    private final EntityDamageEvent.DamageCause damageCause;

    DamageType(EntityDamageEvent.DamageCause damageCause) {
        this.damageCause = damageCause;
    }

    public EntityDamageEvent.DamageCause getDamageCause() {
        return damageCause;
    }

    public static DamageType fromCause(EntityDamageEvent.DamageCause damageCause) {
        return DamageType.PRIMARY;
    }
}
