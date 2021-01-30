package co.runed.bolster.game;

import co.runed.bolster.Bolster;
import co.runed.bolster.util.BukkitUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.projectiles.ProjectileSource;

import java.util.*;

// TODO WHEN ENTITY THAT IS NOT PLAYER DIES REMOVE FROM TEAM
public class Team implements Listener
{
    String name;
    ChatColor color;
    boolean autoAddPlayers = false;
    List<UUID> members = new ArrayList<>();
    List<UUID> onlineMembers = new ArrayList<>();
    List<UUID> players = new ArrayList<>();
    Map<UUID, Integer> kills = new HashMap<>();
    int totalKills = 0;
    boolean allowFriendlyFire;
    boolean removePlayersOnDeath = true;

    public Team(String name, ChatColor color)
    {
        this.name = name;
        this.color = color;
    }

    /**
     * @param entity the entity
     */
    public void add(LivingEntity entity)
    {
        if (this.members.contains(entity.getUniqueId())) return;

        if (entity.getType() == EntityType.PLAYER) this.players.add(entity.getUniqueId());

        this.members.add(entity.getUniqueId());
        this.kills.put(entity.getUniqueId(), 0);

        if ((entity.isValid() && !entity.isDead()) || (entity instanceof Player && ((Player) entity).isOnline()))
        {
            onlineMembers.add(entity.getUniqueId());
        }
    }

    /**
     * @param entity the entity
     */
    public void remove(LivingEntity entity)
    {
        if (!this.members.contains(entity.getUniqueId())) return;

        if (entity.getType() == EntityType.PLAYER) this.players.remove(entity.getUniqueId());

        this.members.remove(entity.getUniqueId());
        this.onlineMembers.remove(entity.getUniqueId());
    }

    public boolean isInTeam(LivingEntity entity)
    {
        return this.members.contains(entity.getUniqueId());
    }

    public List<LivingEntity> getMembers()
    {
        return BukkitUtil.getLivingEntitiesFromUUIDs(this.members);
    }

    public List<LivingEntity> getOnlineMembers()
    {
        return BukkitUtil.getLivingEntitiesFromUUIDs(this.onlineMembers);
    }

    public int getEntityKills(LivingEntity entity)
    {
        if (!this.isInTeam(entity)) return 0;

        return this.kills.get(entity.getUniqueId());
    }

    public int getTotalKills()
    {
        return this.totalKills;
    }

    public int size()
    {
        return this.members.size();
    }

    public int playerCount()
    {
        return this.players.size();
    }

    public void clear()
    {
        this.members.clear();
    }

    /**
     * Sets whether a player should be automatically added to the team when they join the game
     *
     * @param shouldAdd Whether players should be added to the team automatically or not
     */
    public void setAutoAddPlayers(boolean shouldAdd)
    {
        if (shouldAdd && !this.autoAddPlayers)
        {
            Bukkit.getPluginManager().registerEvents(this, Bolster.getInstance());
        }

//        if (!shouldAdd)
//        {
//            HandlerList.unregisterAll(this);
//        }

        this.autoAddPlayers = shouldAdd;
    }

    public boolean allowFriendlyFire()
    {
        return this.allowFriendlyFire;
    }

    public void setAllowFriendlyFire(boolean allow)
    {
        this.allowFriendlyFire = allow;
    }

    public void setRemovePlayersOnDeath(boolean removePlayersOnDeath)
    {
        this.removePlayersOnDeath = removePlayersOnDeath;
    }

    public boolean shouldRemovePlayersOnDeath()
    {
        return removePlayersOnDeath;
    }

    /**
     * Player join event that handles adding players to the team
     *
     * @param event The event
     */
    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event)
    {
        if (this.autoAddPlayers)
        {
            this.add(event.getPlayer());
        }

        Player player = event.getPlayer();
        if (this.isInTeam(event.getPlayer()) && !this.onlineMembers.contains(player.getUniqueId()))
        {
            this.onlineMembers.add(player.getUniqueId());
        }
    }

    @EventHandler
    private void onPlayerLeave(PlayerQuitEvent event)
    {
        this.onlineMembers.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    private void onEntityDeath(EntityDeathEvent event)
    {
        LivingEntity entity = event.getEntity();

        if (!(entity instanceof Player) || shouldRemovePlayersOnDeath()) this.remove(entity);
    }

    @EventHandler
    private void onEntityKill(EntityDeathEvent event)
    {
        Player player = event.getEntity().getKiller();

        if (player == null) return;
        if (!this.isInTeam(player)) return;

        this.totalKills++;
        this.kills.put(player.getUniqueId(), this.kills.get(player.getUniqueId()) + 1);
    }

    @EventHandler
    private void onDamageEntity(EntityDamageByEntityEvent event)
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
        if (!(event.getEntity() instanceof LivingEntity)) return;

        LivingEntity damagee = (LivingEntity) event.getEntity();

        if (this.isInTeam(damagee) && this.isInTeam(entity) && !this.allowFriendlyFire())
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onEntityTarget(EntityTargetLivingEntityEvent event)
    {
        if (!(event.getEntity() instanceof LivingEntity)) return;
        if (event.getTarget() == null) return;

        LivingEntity targeter = (LivingEntity) event.getEntity();
        LivingEntity targeted = (LivingEntity) event.getTarget();

        if (this.isInTeam(targeter) && this.isInTeam(targeted) && !this.allowFriendlyFire())
        {
            event.setCancelled(true);
        }
    }
}
