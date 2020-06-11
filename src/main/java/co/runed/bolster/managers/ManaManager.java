package co.runed.bolster.managers;

import javafx.util.Builder;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ManaManager {
    private final Plugin plugin;
    private final Map<UUID, ManaData> manaData = new HashMap<>();

    public ManaManager(Plugin plugin) {
        this.plugin = plugin;

        //TODO MAKE PASSIVE THAT CAN BE SELECTIVELY APPLIED TO PLAYERS?
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.setLevel((int)this.getCurrentMana(player));
            }
        }, 0L, 20L);
    }

    public void setMaximumMana(LivingEntity entity, float value) {
        this.manaData.putIfAbsent(entity.getUniqueId(), new ManaData());

        ManaData data = this.manaData.get(entity.getUniqueId());

        data.maxMana = value;

        this.manaData.put(entity.getUniqueId(), data);
    }

    public float getMaximumMana(LivingEntity entity) {
        return this.manaData.getOrDefault(entity.getUniqueId(), new ManaData()).maxMana;
    }

    public void addMaximumMana(LivingEntity entity, float value) {
        this.setMaximumMana(entity, this.getMaximumMana(entity) + value);
    }

    public void setCurrentMana(LivingEntity entity, float value) {
        this.manaData.putIfAbsent(entity.getUniqueId(), new ManaData());

        value = Math.min(value, this.getMaximumMana(entity));
        value = Math.max(value, 0);

        ManaData data = this.manaData.get(entity.getUniqueId());

        data.currentMana = value;

        this.manaData.put(entity.getUniqueId(), data);
    }

    public float getCurrentMana(LivingEntity entity) {
        return this.manaData.getOrDefault(entity.getUniqueId(), new ManaData()).currentMana;
    }

    public void addCurrentMana(LivingEntity entity, float value) {
        this.setCurrentMana(entity, this.getCurrentMana(entity) + value);
    }

    private static class ManaData {
        private float maxMana;
        private float currentMana;

        private ManaData() {
            this(0, 0);
        }

        private ManaData(float maxMana, float currentMana) {
            this.maxMana = maxMana;
            this.currentMana = currentMana;
        }
    }
}
