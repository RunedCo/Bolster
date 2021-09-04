package co.runed.bolster.managers;

import co.runed.bolster.Bolster;
import co.runed.bolster.entity.BolsterEntity;
import co.runed.bolster.events.entity.EntityCleanupEvent;
import co.runed.bolster.status.StatusEffect;
import co.runed.bolster.util.TimeUtil;
import co.runed.bolster.util.network.NetworkUtil;
import io.netty.buffer.Unpooled;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.stream.Collectors;

public class StatusEffectManager extends Manager {
    Map<UUID, List<StatusEffect>> currentStatusEffects = new HashMap<>();

    private static StatusEffectManager _instance;

    public StatusEffectManager(Plugin plugin) {
        super(plugin);

        _instance = this;

        Bukkit.getScheduler().runTaskTimer(Bolster.getInstance(), () -> Bukkit.getOnlinePlayers().forEach(this::updateTitleDisplay), 0L, 1L);
    }

    // TODO:
    // CHECK IF HARD CC, IF NOT JUST APPLY CC
    // IF HARD CC CHECK IF HARD CC ALREADY ACTIVE AND CHECK PRIORITY
    // IF PRIORITY HIGHER APPLY INSTEAD OF OTHER CC
    // IF NEW CC EXPIRES FIRST THEN REAPPLY REMAINING CC FOR REST OF DURATION
    public void addStatusEffect(LivingEntity entity, StatusEffect statusEffect) {
        this.clearStatusEffect(entity, statusEffect.getClass());

        var entityEffects = this.getStatusEffects(entity);

        entityEffects.add(statusEffect);

        statusEffect.start(entity);

        if (entity instanceof Player && statusEffect.getId() != null) {
            var player = (Player) entity;
            var byteBuf = Unpooled.buffer();
            NetworkUtil.writeString(byteBuf, statusEffect.getId());

            player.sendPluginMessage(Bolster.getInstance(), "bolster:add_status_effect", byteBuf.array());

            this.updateTitleDisplay(player, true);
        }
    }

    public void clearStatusEffects(LivingEntity entity) {
        this.clearStatusEffects(entity, false);
    }

    public void clearStatusEffects(LivingEntity entity, boolean force) {
        this.clearStatusEffects(entity.getUniqueId(), force);
    }

    public void clearStatusEffects(UUID uuid, boolean force) {
        List<StatusEffect> effects = new ArrayList<>(this.getStatusEffects(uuid));

        for (var effect : effects) {
            this.clearStatusEffect(uuid, effect.getClass(), force);
        }
    }

    public void clearStatusEffect(LivingEntity entity, Class<? extends StatusEffect> statusEffect) {
        this.clearStatusEffect(entity, statusEffect, false);
    }

    public void clearStatusEffect(LivingEntity entity, Class<? extends StatusEffect> statusEffect, boolean force) {
        this.clearStatusEffect(entity.getUniqueId(), statusEffect, force);
    }

    public void clearStatusEffect(UUID uuid, Class<? extends StatusEffect> statusEffect, boolean force) {
        var entityEffects = this.getStatusEffects(uuid);

        StatusEffect effectToRemove = null;
        for (var effect : entityEffects) {
            if (effect.getClass() == statusEffect) {
                effectToRemove = effect;
                break;
            }
        }

        if (effectToRemove != null) {
            effectToRemove.clear(force);
            entityEffects.remove(effectToRemove);
        }
    }

    public void removeStatusEffect(LivingEntity entity, StatusEffect statusEffect) {
        var uuid = entity.getUniqueId();

        if (!this.currentStatusEffects.containsKey(uuid)) return;

        statusEffect.clear();

        if (this.currentStatusEffects.containsKey(uuid)) this.currentStatusEffects.get(uuid).remove(statusEffect);

        if (entity instanceof Player) {
            var player = (Player) entity;

            if (statusEffect.getId() != null) {
                var byteBuf = Unpooled.buffer();
                NetworkUtil.writeString(byteBuf, statusEffect.getId());
                byteBuf.writeDouble(statusEffect.getDuration());

                player.sendPluginMessage(Bolster.getInstance(), "bolster:remove_status_effect", byteBuf.array());
            }

            this.updateTitleDisplay(player, true);
        }
    }

    public List<StatusEffect> getStatusEffects(LivingEntity entity) {
        return this.getStatusEffects(entity.getUniqueId());
    }

    public List<StatusEffect> getStatusEffects(UUID uuid) {
        if (!this.currentStatusEffects.containsKey(uuid)) {
            this.currentStatusEffects.put(uuid, new ArrayList<>());
        }

        return this.currentStatusEffects.get(uuid);
    }

    public boolean hasStatusEffect(LivingEntity entity, Class<? extends StatusEffect> statusEffect) {
        var entityEffects = this.getStatusEffects(entity);

        for (var effect : entityEffects) {
            if (effect.getClass() == statusEffect) {
                return true;
            }
        }

        return false;
    }

    public void updateTitleDisplay(Player player) {
        this.updateTitleDisplay(player, false);
    }

    // TODO unjank
    public void updateTitleDisplay(Player player, boolean force) {
        var display = new StringBuilder();

        Collection<StatusEffect> effects = this.getStatusEffects(player).stream().filter(e -> e.getName() != null && !e.isAmbient()).collect(Collectors.toList());

        if (effects.size() <= 0) {
            if (!force) {
                return;
            }

            display.append("   ");
        }

        for (var effect : effects) {
            display.append(ChatColor.BOLD)
                    .append(effect.getColor()).append(effect.getName().toUpperCase()).append(ChatColor.RESET);

            if (effect.getDuration() < Integer.MAX_VALUE) {
                display.append(" (")
                        .append(TimeUtil.formatDurationHhMmSs(effect.getRemainingDuration()))
                        .append(")");
            }

            display.append(" | ");
        }

        display = new StringBuilder(display.substring(0, display.length() - 3));

        if (display.toString().isEmpty()) display.append("   ");

        BolsterEntity.from(player).sendActionBar(display.toString());
    }

    @EventHandler
    private void onConnect(PlayerJoinEvent event) {
        var uuid = event.getPlayer().getUniqueId();

        if (!this.currentStatusEffects.containsKey(uuid)) return;

        for (var effect : this.currentStatusEffects.get(uuid)) {
            effect.setEntity(event.getPlayer());
        }
    }

    @EventHandler
    private void onEntityDeath(EntityDeathEvent event) {
        this.clearStatusEffects(event.getEntity());
    }

    @EventHandler
    private void onCleanupEntity(EntityCleanupEvent event) {
        if (event.isForced()) {
            this.clearStatusEffects(event.getUniqueId(), true);
        }
    }

    public static StatusEffectManager getInstance() {
        return _instance;
    }
}
