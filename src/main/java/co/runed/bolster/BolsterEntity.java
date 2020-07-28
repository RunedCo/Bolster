package co.runed.bolster;

import co.runed.bolster.classes.BolsterClass;
import co.runed.bolster.properties.Properties;
import co.runed.bolster.status.StatusEffect;
import co.runed.bolster.util.PlayerUtil;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.io.IOException;

public class BolsterEntity
{
    BolsterClass bolsterClass;

    LivingEntity entity;
    Properties properties = new Properties();

    public BolsterEntity(LivingEntity entity)
    {
        this.entity = entity;
    }

    public void setBukkitInstance(LivingEntity entity)
    {
        this.entity = entity;
    }

    public LivingEntity getBukkitInstance()
    {
        return this.entity;
    }

    public Properties getProperties()
    {
        return this.properties;
    }

    public void addStatusEffect(StatusEffect effect, double length, int strength, Object... data)
    {

    }

    public void clearStatusEffect(StatusEffect effect)
    {

    }

    public void sendToServer(String server)
    {
        if (!this.isPlayer()) return;

        try
        {
            PlayerUtil.sendPlayerToServer((Player) this.getBukkitInstance(), server);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public boolean isPlayer()
    {
        return this.entity.getType() == EntityType.PLAYER;
    }

    public boolean isOnline()
    {
        if (this.entity instanceof Player)
        {
            return ((Player) this.entity).isOnline();
        }

        return !this.entity.isDead();
    }
}
