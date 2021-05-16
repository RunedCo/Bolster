package co.runed.bolster.managers;

import co.runed.bolster.BolsterEntity;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
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

        this.remove(entity);
    }

    public static EntityManager getInstance()
    {
        return _instance;
    }
}
