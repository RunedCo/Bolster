package co.runed.bolster.managers;

import co.runed.bolster.status.StatusEffect;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class StatusEffectManager
{
    Map<UUID, List<StatusEffect>> statusEffects = new HashMap<>();

    public StatusEffectManager(Plugin plugin)
    {

    }

    public void addStatusEffect(LivingEntity entity, StatusEffect statusEffect)
    {
        UUID uuid = entity.getUniqueId();

        if (!this.statusEffects.containsKey(uuid)) this.statusEffects.put(uuid, new ArrayList<>());
        this.statusEffects.get(uuid).add(statusEffect);

        statusEffect.start(entity);
    }

    public void clearStatusEffect(LivingEntity entity, Class<? extends StatusEffect> statusEffect)
    {
        UUID uuid = entity.getUniqueId();

        if (!this.statusEffects.containsKey(uuid)) return;

        List<StatusEffect> entityEffects = this.statusEffects.get(uuid);

        for (StatusEffect effect : entityEffects)
        {
            if (effect.getClass() == statusEffect)
            {
                effect.clear();
                entityEffects.remove(effect);
                return;
            }
        }
    }

    public void removeStatusEffect(LivingEntity entity, StatusEffect statusEffect)
    {
        UUID uuid = entity.getUniqueId();

        if (!this.statusEffects.containsKey(uuid)) return;

        statusEffect.clear();

        this.statusEffects.get(uuid).remove(statusEffect);
    }

    public boolean hasStatusEffect(LivingEntity entity, Class<? extends StatusEffect> statusEffect)
    {
        UUID uuid = entity.getUniqueId();

        if (!this.statusEffects.containsKey(uuid)) return false;

        List<StatusEffect> entityEffects = this.statusEffects.get(uuid);

        for (StatusEffect effect : entityEffects)
        {
            if (effect.getClass() == statusEffect)
            {
                return true;
            }
        }

        return false;
    }
}
