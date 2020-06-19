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
    String title;
    List<String> lines = new ArrayList<>();
    List<String> bottomLines = new ArrayList<>();

    int blankLines = 0;

    public Scoreboard getPlayerScoreboard(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        if(playerScoreboards.containsKey(player)) {
            scoreboard = playerScoreboards.get(player);
        }

        player.setScoreboard(scoreboard);

        playerScoreboards.put(player, scoreboard);

        return scoreboard;
    }

    public abstract String getTitle();

    public Sidebar addLine() {
        this.blankLines++;

        return this.addLine(StringUtil.repeat(" ", this.blankLines));
    }

    public Sidebar addLines(List<String> lines) {
        for (String line : lines) {
            this.addLine(line);
        }

        return this;
    }

    public Sidebar addLine(String line) {
        this.lines.add(line);

        return this;
    }

    public Sidebar addSuffixLine() {
        this.blankLines++;

        return this.addSuffixLine(StringUtil.repeat(" ", this.blankLines));
    }

    public Sidebar addSuffixLine(String line) {
        this.bottomLines.add(line);

        return this;
    }

    public Sidebar setUpdateInterval(long interval) {
        long previousInterval = this.getUpdateInterval();

        this.updateInterval = interval;

        if(previousInterval != interval) {
            if(this.updateTask != null || !this.updateTask.isCancelled()) this.updateTask.cancel();

            this.updateTask = this.createUpdateTask();
        }

        return this;
    }

    public long getUpdateInterval() {
        return this.updateInterval;
    }

    public Sidebar addAllPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.addPlayer(player);
        }

        return this;
    }

    public Sidebar addPlayer(Player player) {
        if(this.players.contains(player)) return this;

        this.players.add(player);

        player.setScoreboard(this.getPlayerScoreboard(player));

        if(this.getUpdateInterval() > 0) {
            if(this.updateTask == null || this.updateTask.isCancelled()) {
                this.updateTask = this.createUpdateTask();
            }
        }

        return this;
    }

    public Sidebar removePlayer(Player player) {
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

        return this;
    }

    public void remove() {
        if(this.updateTask != null) {
            this.updateTask.cancel();
        }

        List<Player> playerList = new ArrayList<>(this.players);

        for (Player player : playerList) {
            this.removePlayer(player);
        }
    }

    private BukkitTask createUpdateTask() {
        return Bolster.getInstance().getServer().getScheduler()
                .runTaskTimer(Bolster.getInstance(), this::update, 0L, this.getUpdateInterval());
    }

    private List<String> getAllLines() {
        List<String> scores = new ArrayList<>();

        scores.addAll(this.lines);
        scores.addAll(this.bottomLines);

        return scores;
    }

    private Objective getOrCreateObjective(Scoreboard scoreboard, String title) {
        String id = ChatColor.stripColor(title.toLowerCase());
        Objective existing = scoreboard.getObjective(id);

        if(existing != null) {
            existing.setDisplayName(title);

            return existing;
        }

        return scoreboard.registerNewObjective(id, "dummy", title);
    }

    // SPAGHETTI.
    public void update() {
        for (Player player : this.players) {
            // Resets lines so player specific entries don't show up for everyone
            // A little hacky but it might work well enough...
            this.lines.clear();
            this.bottomLines.clear();
            this.blankLines = 0;

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
        }
    }

    public abstract Sidebar draw(Player player);
}
