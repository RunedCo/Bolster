package co.runed.bolster.match;

import co.runed.bolster.damage.DamageInfo;
import co.runed.bolster.game.Team;
import co.runed.bolster.managers.EntityManager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class PlayerKillMatchHistoryEvent extends AbstractPlayerDamageMatchHistoryEvent {
    private EntityType targetType;
    private UUID targetUUID;
    private List<String> targetTeams;
    private DamageInfo damageInfo;

    public PlayerKillMatchHistoryEvent(Player player, LivingEntity target, DamageInfo damageInfo) {
        super("player_kill", player, target, damageInfo);

        this.targetType = target.getType();
        this.targetUUID = target.getUniqueId();
        this.targetTeams = EntityManager.getInstance().getJoinedTeams(target).stream().map(Team::getName).toList();

        this.damageInfo = damageInfo;
    }
}
