package co.runed.bolster.game;

import co.runed.bolster.Bolster;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.*;

// TODO WHEN ENTITY THAT IS NOT PLAYER DIES REMOVE FROM TEAM
public class Team implements Listener
{
    String name;
    ChatColor color;
    boolean autoAddPlayers = false;
    List<UUID> members = new ArrayList<>();
    List<UUID> players = new ArrayList<>();
    Map<UUID, Integer> kills = new HashMap<>();
    int totalKills = 0;
    boolean allowFriendlyFire;

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
    }

    /**
     * @param entity the entity
     */
    public void remove(LivingEntity entity)
    {
        if (!this.members.contains(entity.getUniqueId())) return;

        if (entity.getType() == EntityType.PLAYER) this.players.remove(entity.getUniqueId());

        this.members.remove(entity.getUniqueId());
    }

    public boolean isInTeam(LivingEntity entity)
    {
        return this.members.contains(entity.getUniqueId());
    }

    public List<LivingEntity> getMembers()
    {
        List<LivingEntity> entities = new ArrayList<>();

        for (UUID uuid : this.members)
        {
            Entity entity = Bukkit.getEntity(uuid);

            if (entity instanceof LivingEntity)
            {
                entities.add((LivingEntity) entity);
            }
        }

        return entities;
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

        if (!shouldAdd)
        {
            HandlerList.unregisterAll(this);
        }

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
    }

    @EventHandler
    private void onEntityDeath(EntityDeathEvent event)
    {
        LivingEntity entity = event.getEntity();

        if (entity instanceof Player) return;

        this.remove(entity);
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
        if (!(event.getDamager() instanceof LivingEntity)) return;
        if (!(event.getEntity() instanceof LivingEntity)) return;

        LivingEntity damagee = (LivingEntity) event.getEntity();
        LivingEntity damager = (LivingEntity) event.getDamager();

        if (this.isInTeam(damagee) && this.isInTeam(damager) && !this.allowFriendlyFire())
        {
            event.setCancelled(true);
        }
    }
}
