package co.runed.bolster.managers;

import co.runed.bolster.BolsterEntity;
import co.runed.bolster.events.CleanupEntityEvent;
import co.runed.bolster.game.Team;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class EntityManager extends Manager
{
    Map<UUID, BolsterEntity> entities = new HashMap<>();
    Map<UUID, List<Team>> teams = new HashMap<>();

    private static EntityManager _instance;

    public EntityManager(Plugin plugin)
    {
        super(plugin);

        _instance = this;
    }

    public BolsterEntity from(LivingEntity entity)
    {
        if (this.entities.containsKey(entity.getUniqueId()))
        {
            BolsterEntity bolsterEntity = this.entities.get(entity.getUniqueId());

            bolsterEntity.setBukkit(entity);

            return bolsterEntity;
        }

        BolsterEntity bolsterEntity = new BolsterEntity(entity);

        this.entities.put(entity.getUniqueId(), bolsterEntity);

        return bolsterEntity;
    }

    public void remove(LivingEntity entity)
    {
        this.remove(entity.getUniqueId());
    }

    public void remove(BolsterEntity entity)
    {
        this.remove(entity.getUniqueId());
    }

    public void remove(UUID uuid)
    {
        if (!this.entities.containsKey(uuid)) return;

        BolsterEntity bolsterEntity = this.entities.remove(uuid);
        bolsterEntity.destroy();
    }

    public Collection<BolsterEntity> getPlayers()
    {
        return this.getAllOfType(EntityType.PLAYER);
    }

    public Collection<BolsterEntity> getAllOfType(EntityType type)
    {
        Collection<BolsterEntity> filtered = new ArrayList<>();

        for (BolsterEntity entity : this.entities.values())
        {
            if (entity.getType() != type) continue;

            filtered.add(entity);
        }

        return filtered;
    }

    public void joinTeam(LivingEntity entity, Team team)
    {
        UUID uuid = entity.getUniqueId();

        if (!this.teams.containsKey(uuid)) this.teams.put(uuid, new ArrayList<>());
        if (this.teams.get(uuid).contains(team)) return;

        this.teams.get(uuid).add(team);

        team.add(entity);
    }

    public void leaveTeam(LivingEntity entity, Team team)
    {
        UUID uuid = entity.getUniqueId();

        if (!this.teams.containsKey(uuid)) return;
        if (!this.teams.get(uuid).contains(team)) return;

        this.teams.get(uuid).remove(team);

        team.remove(entity);
    }

    public List<Team> getJoinedTeams(LivingEntity entity)
    {
        if (!this.teams.containsKey(entity.getUniqueId())) return new ArrayList<>();

        return this.teams.get(entity.getUniqueId());
    }

    // return true if any of entity1's teams are allied with entity2's teams
    public boolean areEntitiesAllied(LivingEntity entity1, LivingEntity entity2)
    {
        for (Team team1 : this.getJoinedTeams(entity1))
        {
            for (Team team2 : this.getJoinedTeams(entity2))
            {
                if (team1.equals(team2) || team1.isAlliedTeam(team2)) return true;
            }
        }

        return false;
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event)
    {
        this.from(event.getPlayer());
    }

    @EventHandler
    private void onEntityRemoved(EntityRemoveFromWorldEvent event)
    {
        if (!(event.getEntity() instanceof LivingEntity)) return;
        if (event.getEntity() instanceof Player) return;

        LivingEntity entity = (LivingEntity) event.getEntity();

        CleanupEntityEvent cleanupEvent = new CleanupEntityEvent(entity, true);
        Bukkit.getServer().getPluginManager().callEvent(cleanupEvent);
    }

    @EventHandler
    private void onCleanupEntity(CleanupEntityEvent event)
    {
        this.remove(event.getUniqueId());
    }

    public static EntityManager getInstance()
    {
        return _instance;
    }
}
