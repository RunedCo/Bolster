package co.runed.bolster.managers;

import co.runed.bolster.BolsterEntity;
import co.runed.bolster.abilities.AbilityProvider;
import co.runed.bolster.abilities.AbilityProviderType;
import co.runed.bolster.classes.BolsterClass;
import co.runed.bolster.util.Manager;
import co.runed.bolster.util.registries.Registries;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class ClassManager extends Manager
{
    //private final Map<UUID, BolsterClass> bolsterClasses = new HashMap<>();

    public static final NamespacedKey CLASS_KEY = new NamespacedKey("bolster", "class");

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
        if (bolsterClass == null)
        {
            AbilityManager.getInstance().reset(entity, AbilityProviderType.CLASS);
        }

        if (bolsterClass != null && bolsterClass.getEntity() != entity)
        {
            if (!bolsterClass.isConfigSet())
            {
                bolsterClass.setConfig(Registries.CLASSES.getConfig(bolsterClass.getId()));
            }

            bolsterClass = (BolsterClass) AbilityManager.getInstance().addProvider(entity, bolsterClass);

            bolsterClass.setEntity(entity);
            bolsterClass.rebuild();
        }
    }

    /**
     * Gets an entity's {@link BolsterClass}
     *
     * @param entity the entity
     * @return the class
     */
    public BolsterClass getClass(LivingEntity entity)
    {
        List<AbilityProvider> providers = AbilityManager.getInstance().getProviders(entity, AbilityProviderType.CLASS);

        return (BolsterClass) providers.stream().filter(prov -> prov instanceof BolsterClass && prov.isEnabled()).findFirst().orElse(null);
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

    // TODO MONITOR PERFORMANCE
    @EventHandler
    private void onChunkLoaded(ChunkLoadEvent event)
    {
        Chunk chunk = event.getChunk();

        this.loadFromChunk(chunk);
    }

    @EventHandler
    private void onWorldLoaded(WorldLoadEvent event)
    {
        for (Chunk chunk : event.getWorld().getLoadedChunks())
        {
            this.loadFromChunk(chunk);
        }
    }

    private void loadFromChunk(Chunk chunk)
    {
        //if (chunk.isLoaded()) return;

        for (Entity entity : chunk.getEntities())
        {
            if (!(entity instanceof LivingEntity)) continue;
            if (entity instanceof Player) continue;

            PersistentDataContainer data = entity.getPersistentDataContainer();

            if (data.has(CLASS_KEY, PersistentDataType.STRING))
            {
                String classKey = data.get(CLASS_KEY, PersistentDataType.STRING);
                BolsterClass bolsterClass = Registries.CLASSES.get(classKey);

                if (bolsterClass == null || BolsterEntity.from((LivingEntity) entity).getBolsterClass() != null)
                    continue;

                BolsterEntity.from((LivingEntity) entity).setBolsterClass(bolsterClass);
            }
        }
    }

    public static ClassManager getInstance()
    {
        return _instance;
    }
}
