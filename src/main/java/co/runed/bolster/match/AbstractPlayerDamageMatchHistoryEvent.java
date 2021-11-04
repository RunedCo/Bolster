package co.runed.bolster.match;

import co.runed.bolster.damage.DamageInfo;
import co.runed.bolster.game.Team;
import co.runed.bolster.managers.EntityManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AbstractPlayerDamageMatchHistoryEvent extends PlayerMatchHistoryEvent {
    private EntityType targetType;
    private UUID targetUUID;
    private List<String> targetTeams = new ArrayList<>();
    private DamageInfo damageInfo;
    private String damageSource;

    public AbstractPlayerDamageMatchHistoryEvent(String id, Player player, Entity target, DamageInfo damageInfo) {
        super(id, player);

        this.targetType = target.getType();
        this.targetUUID = target.getUniqueId();

        if (target instanceof LivingEntity livingEntity) {
            this.targetTeams = EntityManager.getInstance().getJoinedTeams(livingEntity).stream().map(Team::getName).toList();
        }

        this.damageInfo = damageInfo;
        this.damageSource = damageInfo.getDamageSource().getDamageSourceName();
    }
}
