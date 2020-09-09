package co.runed.bolster.managers;

import co.runed.bolster.classes.BolsterClass;
import co.runed.bolster.util.Manager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClassManager extends Manager
{
    private final Map<UUID, BolsterClass> bolsterClasses = new HashMap<>();

    public ClassManager(Plugin plugin)
    {
        super(plugin);
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

        this.bolsterClasses.put(uuid, bolsterClass);

        bolsterClass.setOwner(entity);
    }

    /**
     * Gets an entity's {@link BolsterClass}
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
