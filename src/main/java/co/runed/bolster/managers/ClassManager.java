package co.runed.bolster.managers;

import co.runed.bolster.classes.BolsterClass;
import co.runed.bolster.util.Manager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClassManager extends Manager
{
    private final Map<UUID, BolsterClass> bolsterClasses = new HashMap<>();

    private static ClassManager _instance;

    public ClassManager(Plugin plugin)
    {
        super(plugin);

        _instance = this;
    }

    /**
     * Sets an entity's {@link BolsterClass}
     *
     * @param entity       the entity
     * @param bolsterClass the class
     */
    public void setClass(LivingEntity entity, BolsterClass bolsterClass)
    {
        UUID uuid = entity.getUniqueId();

        if (this.bolsterClasses.containsKey(uuid))
        {
            BolsterClass existingClass = this.bolsterClasses.get(uuid);

            if(existingClass != bolsterClass && existingClass != null) existingClass.destroy();
        }

        this.bolsterClasses.put(uuid, bolsterClass);

        if (bolsterClass != null && bolsterClass.getEntity() != entity) bolsterClass.setEntity(entity);
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

    @EventHandler
    private void onConnect(PlayerJoinEvent event)
    {
        BolsterClass bolsterClass = this.getClass(event.getPlayer());

        if (bolsterClass == null) return;

        bolsterClass.setEntity(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onDeath(EntityDeathEvent event)
    {
        LivingEntity entity = event.getEntity();

        if (entity instanceof Player) return;

        this.reset(entity);
    }

    public static ClassManager getInstance()
    {
        return _instance;
    }
}
