package co.runed.bolster.match;

import co.runed.bolster.damage.DamageInfo;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PlayerDamageMatchHistoryEvent extends AbstractPlayerDamageMatchHistoryEvent {
    public PlayerDamageMatchHistoryEvent(Player player, Entity target, DamageInfo damageInfo) {
        super("player_damage", player, target, damageInfo);
    }
}
