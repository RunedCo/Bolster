package co.runed.bolster.game;

import co.runed.bolster.Bolster;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Team implements Listener
{
    String name;
    ChatColor color;
    boolean autoAddPlayers = false;
    List<UUID> members = new ArrayList<>();

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

        this.members.add(entity.getUniqueId());
    }

    /**
     * @param entity the entity
     */
    public void remove(LivingEntity entity)
    {
        if (!this.members.contains(entity.getUniqueId())) return;

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

    public int size()
    {
        return this.members.size();
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

    /**
     * Player join event that handles adding players to the team
     *
     * @param event The event
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        if (this.autoAddPlayers)
        {
            this.add(event.getPlayer());
        }
    }
}
