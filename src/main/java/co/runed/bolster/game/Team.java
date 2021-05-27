package co.runed.bolster.game;

import co.runed.bolster.Bolster;
import co.runed.bolster.managers.EntityManager;
import co.runed.bolster.util.BukkitUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scoreboard.Scoreboard;

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
    boolean createBukkitTeam;
    List<Team> alliedTeams = new ArrayList<>();

    org.bukkit.scoreboard.Team scoreboardTeam = null;
    boolean isSetup = false;

    public Team(String name, ChatColor color)
    {
        this(name, color, false);
    }

    public Team(String name, ChatColor color, boolean createBukkitTeam)
    {
        this.name = name;
        this.color = color;
        this.createBukkitTeam = createBukkitTeam;
    }

    public void setup()
    {
        if (this.isSetup) return;

        this.isSetup = true;

        Bukkit.getPluginManager().registerEvents(this, Bolster.getInstance());

        if (this.createBukkitTeam)
        {
            Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            org.bukkit.scoreboard.Team team = scoreboard.getTeam(this.name);
            if (team == null) team = scoreboard.registerNewTeam(this.name);

            this.scoreboardTeam = team;
        }
    }

    /**
     * @param entity the entity
     */
    public void add(LivingEntity entity)
    {
        if (!this.isSetup) this.setup();

        if (this.members.contains(entity.getUniqueId())) return;

        EntityManager.getInstance().joinTeam(entity, this);

        if (entity.getType() == EntityType.PLAYER) this.players.add(entity.getUniqueId());

        this.members.add(entity.getUniqueId());
        this.kills.put(entity.getUniqueId(), 0);

        if (this.scoreboardTeam != null && this.createBukkitTeam)
        {
            this.scoreboardTeam.addEntry(entity.getName());
        }

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

        EntityManager.getInstance().leaveTeam(entity, this);

        if (entity.getType() == EntityType.PLAYER) this.players.remove(entity.getUniqueId());

        if (this.scoreboardTeam != null && this.createBukkitTeam)
        {
            this.scoreboardTeam.removeEntry(entity.getName());
        }

        this.members.remove(entity.getUniqueId());
        this.onlineMembers.remove(entity.getUniqueId());
    }

    public boolean isAlliedTeam(Team team)
    {
        if (team.equals(this)) return true;

        return this.alliedTeams.contains(team);
    }

    public void addAlliedTeam(Team team)
    {
        if (this.alliedTeams.contains(team)) return;

        this.alliedTeams.add(team);
    }

    public void removeAlliedTeam(Team team)
    {
        this.alliedTeams.remove(team);
    }

    public org.bukkit.scoreboard.Team getScoreboardTeam()
    {
        if (!this.isSetup) this.setup();

        return scoreboardTeam;
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

    public String getName()
    {
        return this.name;
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
        if (!this.isSetup) this.setup();

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
    @EventHandler(priority = EventPriority.HIGH)
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

    @EventHandler(priority = EventPriority.HIGH)
    private void onPlayerLeave(PlayerQuitEvent event)
    {
        this.onlineMembers.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onEntityDeath(EntityDeathEvent event)
    {
        LivingEntity entity = event.getEntity();

        if (!(entity instanceof Player) || shouldRemovePlayersOnDeath()) this.remove(entity);
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onEntityKill(EntityDeathEvent event)
    {
        Player player = event.getEntity().getKiller();

        if (player == null) return;
        if (!this.isInTeam(player)) return;

        this.totalKills++;
        this.kills.put(player.getUniqueId(), this.kills.get(player.getUniqueId()) + 1);
    }

    @EventHandler(priority = EventPriority.HIGH)
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
            event.setDamage(0);
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onEntityTarget(EntityTargetLivingEntityEvent event)
    {
        if (!(event.getEntity() instanceof LivingEntity)) return;
        if (event.getTarget() == null) return;

        LivingEntity targeter = (LivingEntity) event.getEntity();
        LivingEntity targeted = event.getTarget();

        if (this.isInTeam(targeter) && this.isInTeam(targeted) && !this.allowFriendlyFire())
        {
            event.setCancelled(true);
        }
    }
}
