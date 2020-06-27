package co.runed.bolster.managers;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.abilities.events.EntityCastAbilityEvent;
import co.runed.bolster.abilities.events.EntityPreCastAbilityEvent;
import co.runed.bolster.abilities.listeners.*;
import co.runed.bolster.abilities.properties.AbilityProperties;
import co.runed.bolster.properties.Properties;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class AbilityManager {
    Map<UUID, Map<AbilityTrigger, List<Ability>>> abilities = new HashMap<>();

    public AbilityManager(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(new EntityDamageListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new EntityKillListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new EntityPickupItemListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new EntityShootBowListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerBreakBlockListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerDropItemListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerEatListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerFishListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerOffhandListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerSneakListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerThrowEggListener(), plugin);
    }

    public Ability add(LivingEntity entity, AbilityTrigger trigger, Ability ability) {
        this.abilities.putIfAbsent(entity.getUniqueId(), new HashMap<>());
        this.abilities.get(entity.getUniqueId()).putIfAbsent(trigger, new ArrayList<>());
        this.abilities.get(entity.getUniqueId()).get(trigger).add(ability);

        return ability;
    }

    public void remove(LivingEntity entity, Ability ability) {
        if (entity == null) return;
        if (!this.abilities.containsKey(entity.getUniqueId())) return;

        Map<AbilityTrigger, List<Ability>> abilityMap = this.abilities.get(entity.getUniqueId());

        for (AbilityTrigger trigger : abilityMap.keySet()) {
            abilityMap.get(trigger).remove(ability);
        }

        ability.destroy();
    }

    public List<Ability> getAbilities(LivingEntity entity) {
        List<Ability> abilityList = new ArrayList<>();

        if(!this.abilities.containsKey(entity.getUniqueId())) return abilityList;

        Map<AbilityTrigger, List<Ability>> abilityMap = this.abilities.get(entity.getUniqueId());

        for (AbilityTrigger trigger : abilityMap.keySet()) {
            abilityList.addAll(abilityMap.get(trigger));
        }

        return abilityList;
    }

    public List<Ability> getAbilities(LivingEntity entity, AbilityTrigger trigger) {
        if(!this.abilities.containsKey(entity.getUniqueId())) return new ArrayList<>();
        if(!this.abilities.get(entity.getUniqueId()).containsKey(trigger)) return new ArrayList<>();

        return this.abilities.get(entity.getUniqueId()).get(trigger);
    }

    public void reset(LivingEntity entity) {
        Map<AbilityTrigger, List<Ability>> abilityMap = this.abilities.get(entity.getUniqueId());

        for (List<Ability> abilities : abilityMap.values()) {
            for (Ability ability : abilities) {
                ability.destroy();
            }
        }

        this.abilities.remove(entity.getUniqueId());
        this.abilities.putIfAbsent(entity.getUniqueId(), new HashMap<>());
    }

    public void resetAll() {
        for (UUID uuid : this.abilities.keySet()) {
            Entity entity = Bukkit.getEntity(uuid);

            if(!(entity instanceof LivingEntity)) continue;

            this.reset((LivingEntity)entity);
        }
    }

    public void trigger(LivingEntity entity, AbilityTrigger trigger, Properties properties) {
        EntityPreCastAbilityEvent preCastEvent = new EntityPreCastAbilityEvent(entity, trigger);
        Bukkit.getPluginManager().callEvent(preCastEvent);

        if(preCastEvent.isCancelled()) return;

        List<Ability> abilities = new ArrayList<>(this.getAbilities(entity, trigger));

        properties.set(AbilityProperties.CASTER, entity);

        for (Ability ability : abilities) {
            if(ability == null) continue;

            EntityCastAbilityEvent castEvent = new EntityCastAbilityEvent(entity, ability, trigger, properties);

            Bukkit.getServer().getPluginManager().callEvent(castEvent);

            if(castEvent.isCancelled()) continue;

            boolean success = ability.activate(properties);

            if(ability.getAbilitySource() != null) {
                ability.getAbilitySource().onCastAbility(ability, success);
            }
        }
    }
}
