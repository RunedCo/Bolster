package co.runed.bolster.damage;

import co.runed.dayroom.util.Identifiable;
import co.runed.dayroom.util.Nameable;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public interface DamageSource {
    default String getDamageSourceName() {
        if (this instanceof Nameable nameable) {
            return nameable.getName();
        }

        if (this instanceof Identifiable identifiable) {
            return identifiable.getId();
        }

        return getClass().getName();
    }

    default DamageSource next() {
        return null;
    }

    default Component getDeathMessage(LivingEntity killer, Player victim, DamageInfo damageInfo) {
        return Component.text(victim.getName() + " was slain by " + killer.getName());
    }
}
