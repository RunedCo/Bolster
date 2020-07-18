package co.runed.bolster.managers;

import co.runed.bolster.classes.PlayerClass;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClassManager
{
    private Map<UUID, PlayerClass> playerClasses = new HashMap<>();

    public ClassManager(Plugin plugin)
    {

    }

    public void setClass(LivingEntity entity, PlayerClass playerClass)
    {
        UUID uuid = entity.getUniqueId();

        if (this.playerClasses.containsKey(uuid))
        {
            this.playerClasses.get(uuid).destroy();
        }

        playerClass.setOwner(entity);

        this.playerClasses.put(uuid, playerClass);
    }

    public PlayerClass getClass(LivingEntity entity)
    {
        UUID uuid = entity.getUniqueId();

        if (!this.playerClasses.containsKey(uuid)) return null;

        return this.playerClasses.get(uuid);
    }

    public void reset(LivingEntity entity)
    {
        this.setClass(entity, null);
    }
}
