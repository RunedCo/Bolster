package co.runed.bolster.damage;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public interface DamageSource {
    default DamageSource next() {
        return null;
    }

    default String getDeathMessage(LivingEntity killer, Player victim, DamageInfo damageInfo) {
        return victim.getName() + " was slain by " + killer.getName();
    }
}
