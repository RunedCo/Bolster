package co.runed.bolster.managers;

import co.runed.bolster.classes.BolsterClass;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClassManager
{
    private Map<UUID, BolsterClass> bolsterClasses = new HashMap<>();

    public ClassManager(Plugin plugin)
    {

    }

    /**
     * Sets an entity's {@link BolsterClass}
     *
     * @param entity the entity
     * @param bolsterClass the class
     */
    public void setClass(LivingEntity entity, BolsterClass bolsterClass)
    {
        UUID uuid = entity.getUniqueId();

        if (this.bolsterClasses.containsKey(uuid))
        {
            this.bolsterClasses.get(uuid).destroy();
        }

        bolsterClass.setOwner(entity);

        this.bolsterClasses.put(uuid, bolsterClass);
    }

    /**
     * Gets and entity's {@link BolsterClass}
     *
     * @param entity the entity
     * @return the class
     */
    public BolsterClass getClass(LivingEntity entity)
    {
        UUID uuid = entity.getUniqueId();

        if (!this.bolsterClasses.containsKey(uuid)) return null;

        return this.bolsterClasses.get(uuid);
    }

    /**
     * Resets an entity's {@link BolsterClass}
     *
     * @param entity the entity
     */
    public void reset(LivingEntity entity)
    {
        this.setClass(entity, null);
    }
}
