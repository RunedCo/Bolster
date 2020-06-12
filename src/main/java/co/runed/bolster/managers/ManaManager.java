package co.runed.bolster.managers;

import co.runed.bolster.Bolster;
import javafx.util.Builder;
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

public class ManaManager implements Listener {
    private final Plugin plugin;
    private float defaultMaxMana = 0.0f;
    private final Map<UUID, ManaData> manaData = new HashMap<>();

    public ManaManager(Plugin plugin) {
        this.plugin = plugin;

        Bolster.getInstance().getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    public void setDefaultMaximumMana(float value) {
        this.defaultMaxMana = value;
    }

    public void setMaximumMana(LivingEntity entity, float value) {
        this.manaData.putIfAbsent(entity.getUniqueId(), new ManaData(this.defaultMaxMana));

        ManaData data = this.manaData.get(entity.getUniqueId());

        data.maxMana = value;
        data.currentMana = Math.min(data.currentMana, value);

        this.manaData.put(entity.getUniqueId(), data);

        if(entity.getType() == EntityType.PLAYER) {
            this.updateManaDisplay((Player)entity);
        }
    }

    public float getMaximumMana(LivingEntity entity) {
        return this.manaData.getOrDefault(entity.getUniqueId(), new ManaData(this.defaultMaxMana)).maxMana;
    }

    public void addMaximumMana(LivingEntity entity, float value) {
        this.setMaximumMana(entity, this.getMaximumMana(entity) + value);
    }

    public void setCurrentMana(LivingEntity entity, float value) {
        this.manaData.putIfAbsent(entity.getUniqueId(), new ManaData(this.defaultMaxMana));

        value = Math.min(value, this.getMaximumMana(entity));
        value = Math.max(value, 0);

        ManaData data = this.manaData.get(entity.getUniqueId());

        data.currentMana = value;

        this.manaData.put(entity.getUniqueId(), data);

        if(entity.getType() == EntityType.PLAYER) {
            this.updateManaDisplay((Player)entity);
        }
    }

    public float getCurrentMana(LivingEntity entity) {
        return this.manaData.getOrDefault(entity.getUniqueId(), new ManaData(this.defaultMaxMana)).currentMana;
    }

    public void addCurrentMana(LivingEntity entity, float value) {
        this.setCurrentMana(entity, this.getCurrentMana(entity) + value);
    }

    public void updateManaDisplay(Player player) {
        float currentMana = this.getCurrentMana(player);
        float maxMana = this.getMaximumMana(player);

        player.setLevel((int)currentMana);

        float xpPercent = maxMana > 0 ? (currentMana / maxMana) : 0;

        player.setExp(xpPercent);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        this.updateManaDisplay(player);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        this.updateManaDisplay(player);
    }

    private static class ManaData {
        private float maxMana;
        private float currentMana;

        private ManaData() {
            this(0);
        }

        private ManaData(float maxMana) {
            this(maxMana, 0);
        }

        private ManaData(float maxMana, float currentMana) {
            this.maxMana = maxMana;
            this.currentMana = currentMana;
        }
    }
}
