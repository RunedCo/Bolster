package co.runed.bolster.managers;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.abilities.AbilityProvider;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.abilities.listeners.*;
import co.runed.bolster.entity.BolsterEntity;
import co.runed.bolster.events.EntityCastAbilityEvent;
import co.runed.bolster.events.EntityPreCastAbilityEvent;
import co.runed.bolster.util.Manager;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class AbilityManager extends Manager
{
    Map<UUID, List<AbilityData>> abilities = new HashMap<>();

    private static AbilityManager _instance;

    public AbilityManager(Plugin plugin)
    {
        super(plugin);

        _instance = this;

        Bukkit.getPluginManager().registerEvents(new EntityDamageListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new EntityKillListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new EntityPickupItemListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new EntityShootBowListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new EntitySpawnListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new EntityDeathListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new EntityArmorEquipListener(), plugin);

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
        Bukkit.getPluginManager().registerEvents(new PlayerInventoryClickListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerConnectListener(), plugin);
    }

    public Ability add(LivingEntity entity, AbilityTrigger trigger, Ability ability)
    {
        this.abilities.putIfAbsent(entity.getUniqueId(), new ArrayList<>());

        AbilityData abilityData = new AbilityData(trigger, ability);

        List<AbilityData> datas = this.abilities.get(entity.getUniqueId());

        if (!datas.contains(abilityData)) datas.add(abilityData);

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
        this.trigger(entity, null, trigger, properties);
    }

    public void trigger(LivingEntity entity, AbilityProvider provider, AbilityTrigger trigger, Properties properties)
    {
        if (entity == null) return;

        EntityPreCastAbilityEvent preCastEvent = new EntityPreCastAbilityEvent(entity, trigger);
        Bukkit.getPluginManager().callEvent(preCastEvent);

        if (preCastEvent.isCancelled()) return;

        List<Ability> abilities = new ArrayList<>(this.getAbilities(entity, trigger));
        abilities.sort((a1, a2) -> a2.getPriority() - a1.getPriority());

        properties.set(AbilityProperties.CASTER, BolsterEntity.from(entity));
        properties.set(AbilityProperties.WORLD, entity.getWorld());
        if (properties.contains(AbilityProperties.TARGET)) properties.set(AbilityProperties.INITIAL_TARGET, properties.get(AbilityProperties.TARGET));
        if (trigger != AbilityTrigger.ALL) properties.set(AbilityProperties.TRIGGER, trigger);

        for (Ability ability : abilities)
        {
            if (ability == null) continue;
            if (provider != null && provider != ability.getAbilityProvider()) continue;
            if (!ability.isEnabled()) continue;

            AbilityProvider abilityProvider = provider != null ? provider : ability.getAbilityProvider();

            EntityCastAbilityEvent castEvent = new EntityCastAbilityEvent(entity, ability, trigger, properties);

            Bukkit.getServer().getPluginManager().callEvent(castEvent);

            if (castEvent.isCancelled()) continue;

            boolean success = ability.activate(properties);

            if (abilityProvider != null)
            {
                abilityProvider.onCastAbility(ability, success);
            }
        }

        if (trigger != AbilityTrigger.ALL && trigger != AbilityTrigger.TICK)
            this.trigger(entity, provider, AbilityTrigger.ALL, properties);
    }

    @EventHandler
    private void onTakeDamage(EntityDamageEvent event)
    {
        Entity entity = event.getEntity();

        if (!(entity instanceof LivingEntity)) return;

        for (Ability ability : this.getAbilities((LivingEntity) entity))
        {
            if (!entity.equals(ability.getCaster())) continue;
            if (!ability.isInProgress()) continue;

            if (ability.isCancelledByDamage()) ability.cancel();
        }
    }

    @EventHandler
    private void onDealDamage(EntityDamageByEntityEvent event)
    {
        Entity damager = event.getDamager();
        LivingEntity entity = null;

        if (damager instanceof LivingEntity)
        {
            entity = (LivingEntity) damager;
        }
        else if (damager instanceof Projectile)
        {
            ProjectileSource shooter = ((Projectile) damager).getShooter();

            if (!(shooter instanceof LivingEntity)) return;

            entity = (LivingEntity) shooter;
        }
        else if (damager instanceof TNTPrimed)
        {
            Entity source = ((TNTPrimed) damager).getSource();

            if (!(source instanceof LivingEntity)) return;

            entity = (LivingEntity) source;
        }

        if (entity == null) return;

        for (Ability ability : this.getAbilities(entity))
        {
            if (!entity.equals(ability.getCaster())) continue;
            if (!ability.isInProgress()) continue;

            if (ability.isCancelledByDealingDamage()) ability.cancel();
        }
    }

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();
        Location movedFrom = event.getFrom();
        Location movedTo = event.getTo();

        if ((movedFrom.getX() == movedTo.getX()) && (movedFrom.getY() == movedTo.getY()) && (movedFrom.getZ() == movedTo.getZ()))
            return;

        for (Ability ability : this.getAbilities(player))
        {
            if (!player.equals(ability.getCaster())) continue;
            if (!ability.isInProgress()) continue;

            if (ability.isCancelledByMovement()) ability.cancel();
        }
    }

    @EventHandler
    private void onCastAbility(EntityCastAbilityEvent event)
    {
        Ability ability = event.getAbility();
        if (ability.getTrigger() == null || ability.getTrigger().isPassive()) return;
        if (ability.getCaster() == null) return;

        for (Ability ability2 : this.getAbilities(ability.getCaster()))
        {
            if (ability2.getCaster() == null || ability.getCaster().equals(ability2.getCaster()))
                return;

            if (!ability.getCaster().equals(ability2.getCaster())) continue;
            if (!ability2.isInProgress()) continue;

            if (ability2.isCancelledByCast()) ability2.cancel();
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
                this.task = Bukkit.getServer().getScheduler().runTaskTimer(Bolster.getInstance(), this::run, 0L, 1L);
            }
        }

        protected void run()
        {
            if (ability.getCaster() == null) return;
            if (ability.isOnCooldown()) return;

            Properties properties = new Properties();

            AbilityManager.getInstance().trigger(ability.getCaster(), this.trigger, properties);
        }

        public void destroy()
        {
            ability.destroy();

            if (this.task != null)
            {
                this.task.cancel();
            }
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj instanceof AbilityData)
            {
                AbilityData data = (AbilityData) obj;

                return data.ability.equals(this.ability) && data.trigger.equals(this.trigger);
            }

            return super.equals(obj);
        }
    }

    public static AbilityManager getInstance()
    {
        return _instance;
    }
}
