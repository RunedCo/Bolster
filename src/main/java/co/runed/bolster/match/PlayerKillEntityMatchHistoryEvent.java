package co.runed.bolster.match;

import co.runed.bolster.damage.DamageType;
import co.runed.bolster.game.Team;
import co.runed.bolster.managers.EntityManager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerKillEntityMatchHistoryEvent extends PlayerMatchHistoryEvent {
    EntityType targetType;
    UUID targetUUID;
    List<String> targetTeams = new ArrayList<>();
    DamageType damageType;
    String damageSource;

    public PlayerKillEntityMatchHistoryEvent(Player player, LivingEntity target, String damageSource, DamageType damageType) {
        super("player_kill", player);

        this.targetType = target.getType();
        this.targetUUID = target.getUniqueId();
        this.targetTeams = EntityManager.getInstance().getJoinedTeams(target).stream().map(Team::getName).toList();

        this.damageSource = damageSource;
        this.damageType = damageType;
    }
}
