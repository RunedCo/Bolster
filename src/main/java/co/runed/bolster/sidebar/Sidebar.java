package co.runed.bolster.sidebar;

import co.runed.bolster.Bolster;
import co.runed.bolster.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public abstract class Sidebar {
    private List<Player> players = new ArrayList<>();
    private Map<Player, Scoreboard> playerScoreboards = new HashMap<>();

    private long updateInterval = 10;

    BukkitTask updateTask;

    // Sidebar variables
    List<String> lines = new ArrayList<>();
    List<String> bottomLines = new ArrayList<>();
    int blankLines = 0;

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
            if(this.updateTask != null || !this.updateTask.isCancelled()) this.updateTask.cancel();

            this.updateTask = this.createUpdateTask();
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
     * Adds all players to the sidebar causing it to display on their screen
     */
    public void addAllPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.addPlayer(player);
        }
    }

    /**
     * Adds the player to the sidebar causing it to display on their screen
     * @param player The player
     */
    public void addPlayer(Player player) {
        if(this.players.contains(player)) return;

        this.players.add(player);

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
        this.players.remove(player);

        Scoreboard scoreboard = this.getPlayerScoreboard(player);

        Objective objective = this.getOrCreateObjective(scoreboard, this.getTitle());
        objective.unregister();

        this.playerScoreboards.remove(player);

        if(this.players.size() <= 0) {
            if(this.updateTask != null) {
                this.updateTask.cancel();
            }
        }

        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    /**
     * Removes the update task and all the players from the Sidebar
     */
    public void remove() {
        if(this.updateTask != null) {
            this.updateTask.cancel();
        }

        List<Player> playerList = new ArrayList<>(this.players);

        for (Player player : playerList) {
            this.removePlayer(player);
        }
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
        this.blankLines = 0;
    }

    /**
     * Handles updating the scoreboard display with the layout defined in {@link #draw(Player)}.
     * Updates at the tick speed returned from {@link #getUpdateInterval()}
     */
    public void update() {
        for (Player player : this.players) {
            Sidebar sidebar = this.draw(player);

            Scoreboard scoreboard = this.getPlayerScoreboard(player);
            Objective objective = this.getOrCreateObjective(scoreboard, this.getTitle());

            // Sets objective to display in sidebar
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);

            List<String> lines = sidebar.getAllLines();
            int lineCount = lines.size();

            // Loops through all teams and if the team name is longer than the number of lines remove it
            for (Team team : scoreboard.getTeams()) {
                int count = StringUtil.countMatches(team.getName(), ChatColor.RED.toString());

                if(count > lineCount) team.unregister();
            }

            // Loops through every line and add to sidebar
            // Uses team prefixes to avoid flickering on the scoreboard
            // If we run into issues with running out of characters we can use suffixes as well as prefixes
            for (int i = 0; i < lineCount; i++) {
                String line = lines.get(i);
                int value = lineCount - 1 - i;

                String teamId = StringUtil.repeat(ChatColor.RED.toString(), value + 1);

                // Tries to get team
                Team team = scoreboard.getTeam(teamId);

                if(team == null) {
                    team = scoreboard.registerNewTeam(teamId);
                    team.addEntry(teamId);

                    objective.getScore(teamId).setScore(value);
                }

                if(!team.getPrefix().equals(line)) team.setPrefix(line);
            }

            this.resetLines();
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
