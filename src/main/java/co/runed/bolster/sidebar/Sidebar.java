package co.runed.bolster.sidebar;

import co.runed.bolster.Bolster;
import co.runed.bolster.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public abstract class Sidebar implements Listener {
    private Map<Player, Scoreboard> playerScoreboards = new HashMap<>();

    // Update task variables
    private long updateInterval = 10;
    private boolean autoAddPlayers = false;
    private BukkitTask updateTask;

    // Sidebar lines
    private List<String> lines = new ArrayList<>();
    private List<String> bottomLines = new ArrayList<>();

    /**
     * Gets the player's scoreboard instance for this sidebar
     *
     * @param player The player
     * @return The scoreboard
     */
    public Scoreboard getPlayerScoreboard(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        if(playerScoreboards.containsKey(player)) {
            scoreboard = playerScoreboards.get(player);
        }

        player.setScoreboard(scoreboard);

        playerScoreboards.put(player, scoreboard);

        return scoreboard;
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
    public Sidebar addLine() {
        return this.addLine("");
    }

    /**
     * Add a line to the sidebar
     *
     * @param line The line to add
     * @return The sidebar instance
     */
    public Sidebar addLine(String line) {
        this.lines.add(line);

        return this;
    }

    /**
     * Add a blank line to the sidebar that is always on the bottom
     *
     * @return The sidebar instance
     */
    public Sidebar addSuffixLine() {
        return this.addSuffixLine("");
    }

    /**
     * Add a line to the sidebar that is always on the bottom
     *
     * @param line The line to add
     * @return The sidebar instance
     */
    public Sidebar addSuffixLine(String line) {
        this.bottomLines.add(line);

        return this;
    }

    /**
     * Adds multiple lines to the scoreboard
     *
     * @param lines A list of lines to add
     * @return The sidebar instance
     */
    public Sidebar addLines(List<String> lines) {
        for (String line : lines) {
            this.addLine(line);
        }

        return this;
    }

    /**
     * Sets the update interval of the sidebar
     *
     * @param interval The interval in ticks
     */
    public void setUpdateInterval(long interval) {
        long previousInterval = this.getUpdateInterval();

        this.updateInterval = interval;

        if(previousInterval != interval) {
            if(this.updateTask != null && !this.updateTask.isCancelled()) this.updateTask.cancel();

            this.updateTask = this.createUpdateTask();
        }
    }

    /**
     * Sets whether a player should be automatically added to the sidebar when they join the game
     *
     * @param shouldAdd Whether players should be added to the sidebar automatically or not
     */
    public void setAutoAddPlayers(boolean shouldAdd) {
        if (shouldAdd && !this.autoAddPlayers) {
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
    public void onPlayerJoin(PlayerJoinEvent event) {
        if(this.autoAddPlayers) {
            this.addPlayer(event.getPlayer());
        }
    }

    /**
     * Returns the update interval of the sidebar
     *
     * @return The update interval in ticks
     */
    public long getUpdateInterval() {
        return this.updateInterval;
    }

    /**
     * Adds the player to the sidebar causing it to display on their screen
     * @param player The player
     */
    public void addPlayer(Player player) {
        if (this.playerScoreboards.containsKey(player)) return;

        player.setScoreboard(this.getPlayerScoreboard(player));

        if(this.getUpdateInterval() > 0) {
            if(this.updateTask == null || this.updateTask.isCancelled()) {
                this.updateTask = this.createUpdateTask();
            }
        }
    }

    /**
     * Removes the player from the sidebar causing it to be removed from their screen
     * @param player The player
     */
    public void removePlayer(Player player) {
        Scoreboard scoreboard = this.getPlayerScoreboard(player);

        Objective objective = this.getOrCreateObjective(scoreboard, this.getTitle());
        objective.unregister();

        this.playerScoreboards.remove(player);

        if(this.getPlayers().size() <= 0) {
            if(this.updateTask != null) {
                this.updateTask.cancel();
            }
        }

        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    /**
     * Adds all players to the sidebar causing it to display on their screen
     */
    public void addAllPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.addPlayer(player);
        }
    }

    /**
     * Returns every player that has been added to the sidebar
     *
     * @return A list of players
     */
    public List<Player> getPlayers() {
        return new ArrayList<>(this.playerScoreboards.keySet());
    }

    /**
     * Removes the update task and all the players from the Sidebar
     */
    public void remove() {
        if(this.updateTask != null) {
            this.updateTask.cancel();
        }

        for (Player player : this.getPlayers()) {
            this.removePlayer(player);
        }

        HandlerList.unregisterAll(this);
    }

    /**
     * Creates the task that runs the {@link #update()} function
     *
     * @return The task
     */
    private BukkitTask createUpdateTask() {
        return Bolster.getInstance().getServer().getScheduler()
                .runTaskTimer(Bolster.getInstance(), this::update, 0L, this.getUpdateInterval());
    }

    /**
     * Appends the two row arrays and ensures the bottom rows are on the bottom
     *
     * @return All the scoreboard rows
     */
    private List<String> getAllLines() {
        List<String> scores = new ArrayList<>();

        scores.addAll(this.lines);
        scores.addAll(this.bottomLines);

        return scores;
    }

    /**
     * Gets the objective instance from a scoreboard based on the title
     *
     * @param scoreboard The scoreboard
     * @param title The sidebar title
     * @return The objective
     */
    private Objective getOrCreateObjective(Scoreboard scoreboard, String title) {
        String id = ChatColor.stripColor(title.toLowerCase());
        Objective existing = scoreboard.getObjective(id);

        if(existing != null) {
            existing.setDisplayName(title);

            return existing;
        }

        return scoreboard.registerNewObjective(id, "dummy", title);
    }

    /**
     * Resets lines so player specific entries don't show up for everyone.
     * A little hacky but it should work well enough...
     * Runs at the end of each update call.
     */
    private void resetLines() {
        this.lines.clear();
        this.bottomLines.clear();
    }

    /**
     * Generates a team name based on row number.
     * Team names can be max 16 characters so it changes the {@link ChatColor} symbol used to allow for more than 8 rows
     *
     * @param value The length of the team name
     * @return The team name
     */
    private String getTeamName(int value) {
        ChatColor colorSymbol = ChatColor.values()[value % ChatColor.values().length];

        return StringUtil.repeat(colorSymbol.toString(), (value % 8) + 1);
    }

    /**
     * Handles updating the scoreboard display with the layout defined in {@link #draw(Player)}.
     * Updates at the tick speed returned from {@link #getUpdateInterval()}
     */
    public void update() {
        for (Player player : this.getPlayers()) {
            this.resetLines();

            Sidebar sidebar = this.draw(player);

            Scoreboard scoreboard = this.getPlayerScoreboard(player);
            Objective objective = this.getOrCreateObjective(scoreboard, this.getTitle());

            // Sets objective to display in sidebar
            if(objective.getDisplaySlot() != DisplaySlot.SIDEBAR) objective.setDisplaySlot(DisplaySlot.SIDEBAR);

            List<String> lines = sidebar.getAllLines();
            int lineCount = lines.size();

            // Loops through all teams and if the team name is longer than the number of lines remove it
            for (Team team : scoreboard.getTeams()) {
                int count = team.getName().length() / 2;

                if(count > lineCount) team.unregister();
            }

            // Loops through every line and add to sidebar
            // Uses team prefixes to avoid flickering on the scoreboard
            // If we run into issues with running out of characters we can use suffixes as well as prefixes
            for (int i = 0; i < lineCount; i++) {
                String line = lines.get(i);
                int value = lineCount - 1 - i;

                String teamId = this.getTeamName(value);

                // Tries to get team
                Team team = scoreboard.getTeam(teamId);

                if(team == null) {
                    team = scoreboard.registerNewTeam(teamId);
                    team.addEntry(teamId);

                    objective.getScore(teamId).setScore(value);
                }

                if(!team.getPrefix().equals(line)) team.setPrefix(line);
            }
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
