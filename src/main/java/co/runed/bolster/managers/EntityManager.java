package co.runed.bolster.managers;

import co.runed.bolster.Bolster;
import co.runed.bolster.entity.BolsterLivingEntity;
import co.runed.bolster.entity.BolsterPlayer;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EntityManager implements Listener
{
    Map<UUID, BolsterLivingEntity> entities = new HashMap<>();

    public BolsterLivingEntity adapt(LivingEntity entity)
    {
        if (entities.containsKey(entity.getUniqueId()))
        {
            return entities.get(entity.getUniqueId());
        }

        BolsterLivingEntity bolsterEntity;

        if (entity.getType() == EntityType.PLAYER)
        {
            bolsterEntity = new BolsterPlayer((Player) entity);
        }
        else
        {
            bolsterEntity = new BolsterLivingEntity(entity);
        }

        this.entities.put(entity.getUniqueId(), bolsterEntity);

        return bolsterEntity;
    }

    public void remove(LivingEntity entity)
    {
        if (this.entities.containsKey(entity.getUniqueId()))
        {
            BolsterLivingEntity bolsterEntity = this.from(entity);
        }

        this.entities.remove(entity.getUniqueId());
    }

    public boolean contains(LivingEntity entity)
    {
        return this.entities.containsKey(entity.getUniqueId());
    }

    public static BolsterLivingEntity from(LivingEntity entity)
    {
        return Bolster.getEntityManager().adapt(entity);
    }

    @EventHandler
    private void onEntityRemoved(EntityRemoveFromWorldEvent event)
    {
        Entity entity = event.getEntity();

        if (entity instanceof LivingEntity)
        {
            this.remove((LivingEntity) entity);
        }
    }
}
