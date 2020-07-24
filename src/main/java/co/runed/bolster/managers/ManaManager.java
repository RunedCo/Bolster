package co.runed.bolster.managers;

import co.runed.bolster.Bolster;
import co.runed.bolster.properties.GameProperties;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ManaManager implements Listener
{
    private final Plugin plugin;
    private final Map<UUID, ManaData> manaData = new HashMap<>();

    private float defaultMaxMana = 0.0f;
    private boolean refillOnSpawn = true;
    private boolean enableXpBarDisplay = false;

    public ManaManager(Plugin plugin)
    {
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, this.plugin);
    }

    /**
     * Set the default maximum mana for all entities
     * @param value the default maximum mana
     */
    public void setDefaultMaximumMana(float value)
    {
        this.defaultMaxMana = value;
    }

    /**
     * Set whether an entity should get their maximum mana on spawn
     *
     * @param enabled
     */
    public void setRefillManaOnSpawn(boolean enabled)
    {
        this.refillOnSpawn = enabled;
    }

    /**
     * Set whether the XP bar should be used to show current mana
     *
     * @param enabled
     */
    public void setEnableXpManaBar(boolean enabled)
    {
        this.enableXpBarDisplay = true;

        Bolster.getGameProperties().set(GameProperties.ENABLE_XP, false);
    }

    /**
     * Set an entity's maximum mana
     *
     * @param entity the entity
     * @param value the new maximum mana
     */
    public void setMaximumMana(LivingEntity entity, float value)
    {
        this.manaData.putIfAbsent(entity.getUniqueId(), new ManaData(this.defaultMaxMana));

        ManaData data = this.manaData.get(entity.getUniqueId());

        data.maxMana = value;
        data.currentMana = Math.min(data.currentMana, value);

        this.manaData.put(entity.getUniqueId(), data);

        if (entity.getType() == EntityType.PLAYER)
        {
            this.updateManaDisplay((Player) entity);
        }
    }

    /**
     * Get an entity's maxmium mana
     *
     * @param entity the entity
     * @return the entity's maximum mana
     */
    public float getMaximumMana(LivingEntity entity)
    {
        return this.manaData.getOrDefault(entity.getUniqueId(), new ManaData(this.defaultMaxMana)).maxMana;
    }

    /**
     * Add to an entity's maximum mana
     *
     * @param entity the entity
     * @param value the amount of mana to add
     */
    public void addMaximumMana(LivingEntity entity, float value)
    {
        this.setMaximumMana(entity, this.getMaximumMana(entity) + value);
    }

    /**
     * Set an entity's current mana
     *
     * @param entity the entity
     * @param value the new current mana
     */
    public void setCurrentMana(LivingEntity entity, float value)
    {
        this.manaData.putIfAbsent(entity.getUniqueId(), new ManaData(this.defaultMaxMana));

        value = Math.min(value, this.getMaximumMana(entity));
        value = Math.max(value, 0);

        ManaData data = this.manaData.get(entity.getUniqueId());

        data.currentMana = value;

        this.manaData.put(entity.getUniqueId(), data);

        if (entity.getType() == EntityType.PLAYER)
        {
            this.updateManaDisplay((Player) entity);
        }
    }

    /**
     * Get an entity's current mana
     *
     * @param entity the entity
     * @return the entity's current mana
     */
    public float getCurrentMana(LivingEntity entity)
    {
        return this.manaData.getOrDefault(entity.getUniqueId(), new ManaData(this.defaultMaxMana)).currentMana;
    }

    /**
     * Add to an entity's current mana
     *
     * @param entity the entity
     * @param value the amount of mana to add
     */
    public void addCurrentMana(LivingEntity entity, float value)
    {
        this.setCurrentMana(entity, this.getCurrentMana(entity) + value);
    }

    /**
     * Update the XP bar mana display for a player
     *
     * @param player the player
     */
    public void updateManaDisplay(Player player)
    {
        if (!this.enableXpBarDisplay) return;

        int currentMana = (int) Math.floor(this.getCurrentMana(player));
        float maxMana = this.getMaximumMana(player);

        float xpPercent = maxMana > 0 ? (currentMana / maxMana) : 0;

        player.setExp(Math.min(xpPercent, 0.999f));

        player.setLevel(currentMana);
    }

    @EventHandler
    private void onPlayerRespawn(PlayerRespawnEvent event)
    {
        Player player = event.getPlayer();

        if (this.refillOnSpawn)
        {
            this.setCurrentMana(player, this.getMaximumMana(player));
        }

        this.updateManaDisplay(player);
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();

        if (!this.manaData.containsKey(player.getUniqueId()))
        {
            this.setMaximumMana(player, this.defaultMaxMana);

            if (this.refillOnSpawn) this.setCurrentMana(player, this.getMaximumMana(player));
        }

        this.updateManaDisplay(player);
    }

    private static class ManaData
    {
        private float maxMana;
        private float currentMana;

        private ManaData()
        {
            this(0);
        }

        private ManaData(float maxMana)
        {
            this(maxMana, 0);
        }

        private ManaData(float maxMana, float currentMana)
        {
            this.maxMana = maxMana;
            this.currentMana = currentMana;
        }
    }
}
