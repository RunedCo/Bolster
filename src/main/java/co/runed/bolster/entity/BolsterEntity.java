package co.runed.bolster.entity;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class BolsterEntity<T extends Entity>
{
    T entity;

    public BolsterEntity(T entity)
    {
        this.entity = entity;
    }

    public T bukkit()
    {
        return this.entity;
    }

    public boolean isOnline()
    {
        if (this.entity instanceof Player)
        {
            return ((Player) this.entity).isOnline();
        }

        return !this.entity.isDead();
    }

    public boolean isPlayer()
    {
        return this.entity.getType() == EntityType.PLAYER;
    }

    public UUID getUniqueId()
    {
        return this.entity.getUniqueId();
    }

    public EntityType getType()
    {
        return this.entity.getType();
    }
}
