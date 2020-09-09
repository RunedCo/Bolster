package co.runed.bolster.util.scoreboard.sidebar;

import co.runed.bolster.Bolster;
import co.runed.bolster.util.scoreboard.PacketScoreboard;
import co.runed.bolster.util.scoreboard.Scoreboard;
import co.runed.bolster.util.scoreboard.ScoreboardObjective;
import co.runed.bolster.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.ArrayList;
import java.util.List;

public abstract class Sidebar implements Listener
{
    Scoreboard scoreboard;

    private final List<Player> players = new ArrayList<>();

    // Update task variables
    private long updateInterval = 10;
    private boolean autoAddPlayers = false;
    private BukkitTask updateTask;
    private int blankLineCount = 0;

    // Sidebar lines
    private final List<String> lines = new ArrayList<>();
    private final List<String> bottomLines = new ArrayList<>();

    public Sidebar()
    {
        this.scoreboard = new PacketScoreboard(Bolster.getInstance());
    }

    /**
     * Returns the title of the sidebar
     *
     * @return The title
     */
    public abstract String getTitle();

    /**
     * Add a blank line to the sidebar
     *
     * @return The sidebar instance
     */
    public Sidebar addLine()
    {
        this.lines.add(StringUtil.repeat(" ", this.blankLineCount++));

        return this;
    }

    /**
     * Add a line to the sidebar
     *
     * @param line The line to add
     * @return The sidebar instance
     */
    public Sidebar addLine(String line)
    {
        this.addLines(StringUtil.formatLore(line, 24));

        return this;
    }

    /**
     * Add a blank line to the sidebar that is always on the bottom
     *
     * @return The sidebar instance
     */
    public Sidebar addSuffixLine()
    {
        return this.addSuffixLine(StringUtil.repeat(" ", this.blankLineCount++));
    }

    /**
     * Add a line to the sidebar that is always on the bottom
     *
     * @param line The line to add
     * @return The sidebar instance
     */
    public Sidebar addSuffixLine(String line)
    {
        this.bottomLines.add(line);

        return this;
    }

    /**
     * Adds multiple lines to the scoreboard
     *
     * @param lines A list of lines to add
     * @return The sidebar instance
     */
    public Sidebar addLines(List<String> lines)
    {
        this.lines.addAll(lines);

        return this;
    }

    /**
     * Sets the update interval of the sidebar
     *
     * @param interval The interval in ticks
     */
    public void setUpdateInterval(long interval)
    {
        long previousInterval = this.getUpdateInterval();

        this.updateInterval = interval;

        if (previousInterval != interval)
        {
            if (this.updateTask != null && !this.updateTask.isCancelled()) this.updateTask.cancel();

            this.updateTask = this.createUpdateTask();
        }
    }

    /**
     * Sets whether a player should be automatically added to the sidebar when they join the game
     *
     * @param shouldAdd Whether players should be added to the sidebar automatically or not
     */
    public void setAutoAddPlayers(boolean shouldAdd)
    {
        if (shouldAdd && !this.autoAddPlayers)
        {
            Bukkit.getPluginManager().registerEvents(this, Bolster.getInstance());
        }

        this.autoAddPlayers = shouldAdd;
    }

    /**
     * Player join event that handles adding players to the sidebar
     *
     * @param event The event
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        if (this.autoAddPlayers)
        {
            Bolster.getSidebarManager().setSidebar(event.getPlayer(), this);
        }
    }

    /**
     * Returns the update interval of the sidebar
     *
     * @return The update interval in ticks
     */
    public long getUpdateInterval()
    {
        return this.updateInterval;
    }

    /**
     * Adds the player to the sidebar causing it to display on their screen
     *
     * @param player The player
     */
    public void addPlayer(Player player)
    {
        if (this.players.contains(player)) return;

        Bolster.getSidebarManager().clearSidebar(player);

        this.players.add(player);

        if (this.getUpdateInterval() > 0)
        {
            if (this.updateTask == null || this.updateTask.isCancelled())
            {
                this.updateTask = this.createUpdateTask();
            }
        }
    }

    /**
     * Removes the player from the sidebar causing it to be removed from their screen
     *
     * @param player The player
     */
    public void removePlayer(Player player)
    {
        if (!this.players.contains(player)) return;

        ScoreboardObjective objective = this.getOrCreateObjective(player, this.getTitle());
        objective.unsubscribe(player);

        this.players.remove(player);

        if (this.players.size() <= 0)
        {
            if (this.updateTask != null)
            {
                this.updateTask.cancel();
            }
        }
    }

    /**
     * Adds all players to the sidebar causing it to display on their screen
     */
    public void addAllPlayers()
    {
        for (Player player : Bukkit.getOnlinePlayers())
        {
            Bolster.getSidebarManager().setSidebar(player, this);
        }
    }

    /**
     * Removes the update task and all the players from the Sidebar
     */
    public void remove()
    {
        if (this.updateTask != null)
        {
            this.updateTask.cancel();
        }

        for (Player player : this.players)
        {
            this.removePlayer(player);
        }

        HandlerList.unregisterAll(this);
    }

    /**
     * Creates the task that runs the {@link #update()} function
     *
     * @return The task
     */
    private BukkitTask createUpdateTask()
    {
        return Bolster.getInstance().getServer().getScheduler()
                .runTaskTimer(Bolster.getInstance(), this::update, 0L, this.getUpdateInterval());
    }

    /**
     * Appends the two row arrays and ensures the bottom rows are on the bottom
     *
     * @return All the scoreboard rows
     */
    private List<String> getLines()
    {
        List<String> scores = new ArrayList<>();

        scores.addAll(this.lines);
        scores.addAll(this.bottomLines);

        return scores;
    }

    /**
     * Gets the objective instance from a scoreboard based on the title
     *
     * @param title The sidebar title
     * @return The objective
     */
    private ScoreboardObjective getOrCreateObjective(Player player, String title)
    {
        ScoreboardObjective existing = scoreboard.getPlayerObjective(player, title);

        if (existing != null)
        {
            existing.setDisplayName(title);

            return existing;
        }

        return scoreboard.createPlayerObjective(player, title, DisplaySlot.SIDEBAR);
    }

    /**
     * Resets lines so player specific entries don't show up for everyone.
     * A little hacky but it should work well enough...
     * Runs at the end of each update call.
     */
    private void resetLines()
    {
        this.lines.clear();
        this.bottomLines.clear();
        this.blankLineCount = 0;
    }

    /**
     * Handles updating the scoreboard display with the layout defined in {@link #draw(Player)}.
     * Updates at the tick speed returned from {@link #getUpdateInterval()}
     */
    public void update()
    {
        for (Player player : this.players)
        {
            this.resetLines();

            Sidebar sidebar = this.draw(player);

            ScoreboardObjective objective = this.getOrCreateObjective(player, this.getTitle());

            objective.applyLines(sidebar.getLines());
        }
    }

    /**
     * Called in {@link #update()} and used to set the player specific sidebar values
     *
     * @param player Player to show sidebar to
     * @return Sidebar instance to be rendered
     */
    public abstract Sidebar draw(Player player);
}
