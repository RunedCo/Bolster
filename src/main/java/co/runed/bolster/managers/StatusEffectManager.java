package co.runed.bolster.managers;

import co.runed.bolster.status.StatusEffect;
import co.runed.bolster.util.Manager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class StatusEffectManager extends Manager
{
    Map<UUID, List<StatusEffect>> statusEffects = new HashMap<>();

    private static StatusEffectManager _instance;

    public StatusEffectManager(Plugin plugin)
    {
        super(plugin);

        _instance = this;
    }

    public void addStatusEffect(LivingEntity entity, StatusEffect statusEffect)
    {
        this.clearStatusEffect(entity, statusEffect.getClass());

        List<StatusEffect> entityEffects = this.getStatusEffects(entity);

        entityEffects.add(statusEffect);
        statusEffect.start(entity);

        if (entity.getType() == EntityType.PLAYER)
        {
            this.updateTitleDisplay((Player) entity);
        }
    }

    public void clearStatusEffects(LivingEntity entity)
    {
        List<StatusEffect> effects = new ArrayList<>(this.getStatusEffects(entity));

        for (StatusEffect effect : effects)
        {
            this.clearStatusEffect(entity, effect.getClass());
        }
    }

    public void clearStatusEffect(LivingEntity entity, Class<? extends StatusEffect> statusEffect)
    {
        List<StatusEffect> entityEffects = this.getStatusEffects(entity);

        StatusEffect effectToRemove = null;
        for (StatusEffect effect : entityEffects)
        {
            if (effect.getClass() == statusEffect)
            {
                effectToRemove = effect;
                break;
            }
        }

        if (effectToRemove != null)
        {
            effectToRemove.clear();
            entityEffects.remove(effectToRemove);
        }
    }

    public void removeStatusEffect(LivingEntity entity, StatusEffect statusEffect)
    {
        UUID uuid = entity.getUniqueId();

        if (!this.statusEffects.containsKey(uuid)) return;

        statusEffect.clear();

        this.statusEffects.get(uuid).remove(statusEffect);
    }

    public List<StatusEffect> getStatusEffects(LivingEntity entity)
    {
        UUID uuid = entity.getUniqueId();

        if (!this.statusEffects.containsKey(uuid))
        {
            this.statusEffects.put(uuid, new ArrayList<>());
        }

        return this.statusEffects.get(uuid);
    }

    public boolean hasStatusEffect(LivingEntity entity, Class<? extends StatusEffect> statusEffect)
    {
        List<StatusEffect> entityEffects = this.getStatusEffects(entity);

        for (StatusEffect effect : entityEffects)
        {
            if (effect.getClass() == statusEffect)
            {
                return true;
            }
        }

        return false;
    }

    public void updateTitleDisplay(Player player)
    {
        StringBuilder display = new StringBuilder();

        for (StatusEffect effect : this.getStatusEffects(player))
        {
            if (effect.getName() == null) continue;

            display.append(effect.getColor()).append(effect.getName().toUpperCase()).append(" (").append(effect.getDuration()).append(")").append(", ");
        }

        display = new StringBuilder(display.substring(0, display.length() - 2));

        //PlayerUtil.sendActionBar(player, ChatColor.BOLD + display.toString());

        //player.sendTitle("", display.toString(), 0, 10, 0);
    }

    @EventHandler
    private void onConnect(PlayerJoinEvent event)
    {
        UUID uuid = event.getPlayer().getUniqueId();

        if (!this.statusEffects.containsKey(uuid)) return;

        for (StatusEffect effect : this.statusEffects.get(uuid))
        {
            effect.setEntity(event.getPlayer());
        }
    }

    @EventHandler
    private void onEntityDeath(EntityDeathEvent event)
    {
        this.clearStatusEffects(event.getEntity());
    }

    public static StatusEffectManager getInstance()
    {
        return _instance;
    }
}
