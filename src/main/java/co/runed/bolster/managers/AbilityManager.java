package co.runed.bolster.managers;

import co.runed.bolster.BolsterEntity;
import co.runed.bolster.abilities.*;
import co.runed.bolster.events.EntityCastAbilityEvent;
import co.runed.bolster.events.EntityPreCastAbilityEvent;
import co.runed.bolster.listeners.*;
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

import java.util.*;
import java.util.stream.Collectors;

public class AbilityManager extends Manager
{
    Map<UUID, List<AbilityProviderData>> providers = new HashMap<>();

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

    public AbilityProvider addProvider(LivingEntity entity, AbilityProvider provider)
    {
        if (provider == null) return null;

        AbilityProviderType type = provider.getType();
        UUID uuid = entity.getUniqueId();

        this.providers.putIfAbsent(uuid, new ArrayList<>());
        List<AbilityProviderData> provList = this.providers.get(uuid);

        boolean exists = false;
        for (AbilityProviderData existingData : provList)
        {
            // check if existing exists and is enabled, if not enable
            if (existingData.provider.getClass().equals(provider.getClass()))
            {
                provider = existingData.provider;
                exists = true;

                continue;
            }

            // check if type is solo - if so disable all others
            if (type.isSolo() && existingData.type.equals(type))
            {
                existingData.provider.setEnabled(false);
            }
        }

        if (!exists)
        {
            provList.add(new AbilityProviderData(provider, type));
        }
// TODO keep an eye on this
//        if (exists)
//        {
//            provider.setEnabled(false);
//        }

        provider.setEnabled(true);
        provider.setEntity(entity);

        return provider;
    }

    public void removeProvider(LivingEntity entity, AbilityProvider provider)
    {
        AbilityProviderType type = provider.getType();
        UUID uuid = entity.getUniqueId();

        if (!this.providers.containsKey(uuid)) return;

        List<AbilityProviderData> provList = this.providers.get(uuid);

        for (AbilityProviderData existingData : provList)
        {
            // check if existing exists and is enabled, if not enable
            if (existingData.provider.getClass().equals(provider.getClass()))
            {
                existingData.provider.setEnabled(false);
            }
        }
    }

    public AbilityProvider getProvider(LivingEntity entity, AbilityProviderType type, String id)
    {
        if (id == null) return null;

        return this.getProviders(entity).stream().filter((prov) -> prov.getType().equals(type) && prov.getId().equals(id)).findFirst().orElse(null);
    }

    public boolean hasProvider(LivingEntity entity, AbilityProviderType type, String id)
    {
        if (id == null) return false;

        return this.getProviders(entity).stream().anyMatch((prov) -> prov.getType().equals(type) && prov.getId().equals(id));
    }

    public boolean hasProvider(LivingEntity entity, AbilityProvider provider)
    {
        if (provider == null) return false;

        return this.getProviders(entity).stream().anyMatch((prov) -> prov.getClass().equals(provider.getClass()));
    }

    public boolean hasExactProvider(LivingEntity entity, AbilityProvider provider)
    {
        return this.getProviders(entity).contains(provider);
    }

    public List<AbilityProvider> getProviders(LivingEntity entity)
    {
        UUID uuid = entity.getUniqueId();

        List<AbilityProvider> providerList = new ArrayList<>();

        if (!this.providers.containsKey(uuid)) return providerList;

        return this.providers.get(uuid).stream().map((data) -> data.provider).collect(Collectors.toList());
    }

    public List<AbilityProvider> getProviders(LivingEntity entity, AbilityProviderType type)
    {
        return this.getProviders(entity).stream().filter((provider) -> provider.getType() == type).collect(Collectors.toList());
    }

    public List<AbilityProvider.AbilityData> getAbilityData(LivingEntity entity)
    {
        List<AbilityProvider.AbilityData> abilityList = new ArrayList<>();

        for (AbilityProvider provider : this.getProviders(entity))
        {
            abilityList.addAll(provider.getAbilities());
        }

        return abilityList;
    }

    public List<AbilityProvider.AbilityData> getAbilityData(LivingEntity entity, AbilityTrigger trigger)
    {
        return this.getAbilityData(entity).stream().filter((data) -> data.trigger == trigger).collect(Collectors.toList());
    }

    public List<Ability> getAbilities(LivingEntity entity)
    {
        return this.getAbilityData(entity).stream().map((data) -> data.ability).collect(Collectors.toList());
    }

    public List<Ability> getAbilities(LivingEntity entity, AbilityTrigger trigger)
    {
        return this.getAbilityData(entity, trigger).stream().map((data) -> data.ability).collect(Collectors.toList());
    }

    public boolean hasAbilities(LivingEntity entity, AbilityTrigger trigger)
    {
        return this.getAbilities(entity, trigger).size() > 0;
    }

    public void reset(LivingEntity entity)
    {
        for (AbilityProvider provider : this.getProviders(entity))
        {
            provider.setEnabled(false);
        }
    }

    public void reset(LivingEntity entity, AbilityProviderType type)
    {
        for (AbilityProvider provider : this.getProviders(entity, type))
        {
            provider.setEnabled(false);
        }
    }

    public void destroy(LivingEntity entity)
    {
        for (AbilityProvider provider : this.getProviders(entity))
        {
            provider.destroy();
        }

        this.providers.remove(entity.getUniqueId());
    }

    public void destroy(LivingEntity entity, AbilityProviderType type)
    {
        for (AbilityProvider provider : this.getProviders(entity, type))
        {
            provider.destroy();
        }

        if (this.providers.containsKey(entity.getUniqueId()))
        {
            this.providers.get(entity.getUniqueId()).removeIf((d) -> d.type == type);
        }
    }

    public void resetAll()
    {
        for (UUID uuid : this.providers.keySet())
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

        EntityPreCastAbilityEvent preCastEvent = new EntityPreCastAbilityEvent(entity, provider, trigger, properties);
        Bukkit.getPluginManager().callEvent(preCastEvent);

        if (preCastEvent.isCancelled()) return;

        List<Ability> abilities = new ArrayList<>(this.getAbilities(entity, trigger));
        abilities.sort((a1, a2) -> a2.getPriority() - a1.getPriority());

        properties.set(AbilityProperties.CASTER, BolsterEntity.from(entity));
        properties.set(AbilityProperties.WORLD, entity.getWorld());

        if (provider != null || !properties.contains(AbilityProperties.ABILITY_PROVIDER))
        {
            properties.set(AbilityProperties.ABILITY_PROVIDER, provider);
        }

        if (properties.contains(AbilityProperties.TARGET))
        {
            properties.set(AbilityProperties.INITIAL_TARGET, properties.get(AbilityProperties.TARGET));
        }

        if (trigger != AbilityTrigger.ALL) properties.set(AbilityProperties.TRIGGER, trigger);

        for (Ability ability : abilities)
        {
            if (ability == null) continue;
            if (provider != null && provider != ability.getAbilityProvider()) continue;
            if (!ability.isEnabled()) continue;
            if (properties.get(AbilityProperties.IS_CANCELLED) && ability.shouldSkipIfCancelled()) continue;

            AbilityProvider abilityProvider = provider != null ? provider : ability.getAbilityProvider();

            EntityCastAbilityEvent castEvent = new EntityCastAbilityEvent(entity, ability, trigger, properties);
            Bukkit.getServer().getPluginManager().callEvent(castEvent);

            if (castEvent.isCancelled()) continue;

            boolean success = ability.activate(properties);

            if (ability.getTrigger() != AbilityTrigger.TICK && ability.getTrigger() != AbilityTrigger.ON_CAST_ABILITY && trigger != AbilityTrigger.ALL)
            {
                Properties onCastProperties = new Properties(properties);
                onCastProperties.set(AbilityProperties.ABILITY, ability);

                this.trigger(entity, provider, AbilityTrigger.ON_CAST_ABILITY, onCastProperties);
            }

            if (abilityProvider != null)
            {
                abilityProvider.onCastAbility(ability, success);
            }
        }

        if (trigger != AbilityTrigger.ALL && trigger != AbilityTrigger.TICK)
        {
            this.trigger(entity, provider, AbilityTrigger.ALL, properties);
        }
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

            if (ability.isCancelledByTakingDamage())
            {
                System.out.println("Cancelled " + ability.getId() + " for " + ability.getCaster().getName() + " reason: take damage");

                ability.cancel();
            }
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
        //TODO CHECKS TO MAKE SURE DAMAGE IS EITHER ACTUAALLY A SWING OR HITTING WITH A BOW. MAYBE ADD MORE EDGE CASES HERE FOR TNT AND POTIONS
        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK && event.getCause() != EntityDamageEvent.DamageCause.PROJECTILE)
            return;

        for (Ability ability : this.getAbilities(entity))
        {
            if (!entity.equals(ability.getCaster())) continue;
            if (!ability.isInProgress()) continue;

            if (ability.isCancelledByDealingDamage())
            {
                System.out.println("Cancelled " + ability.getId() + " for " + ability.getCaster().getName() + " reason: deal damage");

                ability.cancel();
            }
        }
    }

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();
        Location movedFrom = event.getFrom().clone();
        Location movedTo = event.getTo().clone();
        double movementThreshold = 0.02;
        double distance = movedTo.subtract(movedFrom).toVector().length();

        if (distance <= movementThreshold && distance >= -movementThreshold)
            return;

        for (Ability ability : this.getAbilities(player))
        {
            if (!player.equals(ability.getCaster())) continue;
            if (!ability.isInProgress()) continue;

            if (ability.isCancelledByMovement())
            {
                System.out.println("Cancelled " + ability.getId() + " for " + ability.getCaster().getName() + " reason: move");

                ability.cancel();
            }
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

            if (ability2.isCancelledByCast())
            {
                System.out.println("Cancelled " + ability.getId() + " for " + ability.getCaster().getName() + " reason: cast");

                ability2.cancel();
            }
        }
    }

    private static class AbilityProviderData
    {
        private AbilityProvider provider;
        private AbilityProviderType type;

        public AbilityProviderData(AbilityProvider provider, AbilityProviderType type)
        {
            this.provider = provider;
            this.type = type;
        }
    }

    public static AbilityManager getInstance()
    {
        return _instance;
    }
}
