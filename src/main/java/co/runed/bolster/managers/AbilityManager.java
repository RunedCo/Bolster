package co.runed.bolster.managers;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.abilities.listeners.*;
import co.runed.bolster.events.EntityCastAbilityEvent;
import co.runed.bolster.events.EntityPreCastAbilityEvent;
import co.runed.bolster.util.Manager;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class AbilityManager extends Manager
{
    Map<UUID, List<AbilityData>> abilities = new HashMap<>();

    public AbilityManager(Plugin plugin)
    {
        super(plugin);
        
        Bukkit.getPluginManager().registerEvents(new EntityDamageListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new EntityKillListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new EntityPickupItemListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new EntityShootBowListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new EntitySpawnListener(), plugin);

        Bukkit.getPluginManager().registerEvents(new PlayerBreakBlockListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerDropItemListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerEatListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerFishListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerOffhandListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerSneakListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerThrowEggListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerInteractAtEntityListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerSelectListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerPortalListener(), plugin);
    }

    public Ability add(LivingEntity entity, AbilityTrigger trigger, Ability ability)
    {
        this.abilities.putIfAbsent(entity.getUniqueId(), new ArrayList<>());

        AbilityData abilityData = new AbilityData(trigger, ability);

        this.abilities.get(entity.getUniqueId()).add(abilityData);

        ability.setTrigger(trigger);

        return ability;
    }

    public void remove(LivingEntity entity, Ability ability)
    {
        if (entity == null) return;
        if (!this.abilities.containsKey(entity.getUniqueId())) return;

        List<AbilityData> abilities = this.abilities.get(entity.getUniqueId());

        Optional<AbilityData> filtered = abilities.stream().filter((data) -> data.ability == ability).findFirst();

        if (!filtered.isPresent()) return;

        AbilityData data = filtered.get();

        abilities.remove(data);

        data.destroy();
    }

    public List<Ability> getAbilities(LivingEntity entity)
    {
        List<Ability> abilityList = new ArrayList<>();

        if (!this.abilities.containsKey(entity.getUniqueId())) return abilityList;

        List<AbilityData> abilities = this.abilities.get(entity.getUniqueId());

        for (AbilityData data : abilities)
        {
            abilityList.add(data.ability);
        }

        return abilityList;
    }

    public List<Ability> getAbilities(LivingEntity entity, AbilityTrigger trigger)
    {
        List<Ability> abilityList = new ArrayList<>();

        if (!this.abilities.containsKey(entity.getUniqueId())) return abilityList;

        List<AbilityData> abilities = this.abilities.get(entity.getUniqueId());

        for (AbilityData data : abilities)
        {
            if (data.trigger == trigger) abilityList.add(data.ability);
        }

        return abilityList;
    }

    public boolean hasAbilities(LivingEntity entity, AbilityTrigger trigger)
    {
        return this.getAbilities(entity, trigger).size() > 0;
    }

    public void reset(LivingEntity entity)
    {
        List<AbilityData> abilityData = this.abilities.get(entity.getUniqueId());

        for (AbilityData data : abilityData)
        {
            data.destroy();
        }

        this.abilities.remove(entity.getUniqueId());
        this.abilities.putIfAbsent(entity.getUniqueId(), new ArrayList<>());
    }

    public void resetAll()
    {
        for (UUID uuid : this.abilities.keySet())
        {
            Entity entity = Bukkit.getEntity(uuid);

            if (!(entity instanceof LivingEntity)) continue;

            this.reset((LivingEntity) entity);
        }
    }

    public void trigger(LivingEntity entity, AbilityTrigger trigger, Properties properties)
    {
        EntityPreCastAbilityEvent preCastEvent = new EntityPreCastAbilityEvent(entity, trigger);
        Bukkit.getPluginManager().callEvent(preCastEvent);

        if (preCastEvent.isCancelled()) return;

        List<Ability> abilities = new ArrayList<>(this.getAbilities(entity, trigger));

        properties.set(AbilityProperties.CASTER, EntityManager.from(entity));

        for (Ability ability : abilities)
        {
            if (ability == null) continue;

            EntityCastAbilityEvent castEvent = new EntityCastAbilityEvent(entity, ability, trigger, properties);

            Bukkit.getServer().getPluginManager().callEvent(castEvent);

            if (castEvent.isCancelled()) continue;

            boolean success = ability.activate(properties);

            if (ability.getAbilitySource() != null)
            {
                ability.getAbilitySource().onCastAbility(ability, success);
            }
        }
    }

    public static class AbilityData
    {
        AbilityTrigger trigger;
        Ability ability;
        BukkitTask task = null;

        public AbilityData(AbilityTrigger trigger, Ability ability)
        {
            this.trigger = trigger;
            this.ability = ability;

            if (this.trigger == AbilityTrigger.TICK)
            {
                this.task = Bukkit.getServer().getScheduler().runTaskTimer(Bolster.getInstance(), this::run, 0L, (long) (ability.getCooldown() * 20));
            }
        }

        protected void run()
        {
            if (ability.getCaster() == null) return;

            Properties properties = new Properties();
            properties.set(AbilityProperties.CASTER, ability.getCaster());
            properties.set(AbilityProperties.WORLD, ability.getCaster().getWorld());

            Bolster.getAbilityManager().trigger(ability.getCaster(), this.trigger, properties);
        }

        public void destroy()
        {
            ability.destroy();

            if (this.task != null)
            {
                this.task.cancel();
            }
        }
    }
}
