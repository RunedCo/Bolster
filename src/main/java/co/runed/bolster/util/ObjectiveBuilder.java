package co.runed.bolster.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;

public class ObjectiveBuilder {
    Scoreboard scoreboard;
    Objective objective;
    List<String> lines = new ArrayList<>();
    List<String> bottomLines = new ArrayList<>();

    int blankLines = 0;

    public ObjectiveBuilder() {
        this(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    public ObjectiveBuilder(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    public ObjectiveBuilder setTitle(String title) {
        String id = ChatColor.stripColor(title.toLowerCase());

        if(this.scoreboard.getObjective(id) != null) {
            this.objective = this.scoreboard.getObjective(id);
            return this;
        }

        this.objective = this.scoreboard.registerNewObjective(id, "dummy", title);
        return this;
    }

    public ObjectiveBuilder setDisplaySlot(DisplaySlot slot) {
        this.objective.setDisplaySlot(slot);

        return this;
    }

    public ObjectiveBuilder setRenderType(RenderType render) {
        this.objective.setRenderType(render);

        return this;
    }

    public ObjectiveBuilder addLine() {
        this.blankLines++;

        return this.addLine(new String(new char[this.blankLines]).replace("\0", " "));
    }

    public ObjectiveBuilder addLines(List<String> lines) {
        for (String line : lines) {
            this.addLine(line);
        }

        return this;
    }

    public ObjectiveBuilder addLine(String line) {
        this.lines.add(line);

        return this;
    }

    public ObjectiveBuilder addLineBottom() {
        this.blankLines++;

        return this.addLineBottom(new String(new char[this.blankLines]).replace("\0", " "));
    }

    public ObjectiveBuilder addLineBottom(String line) {
        this.bottomLines.add(line);

        return this;
    }

    private List<String> getAllLines() {
        List<String> scores = new ArrayList<>();

        scores.addAll(this.lines);
        scores.addAll(this.bottomLines);

        return scores;
    }

    private List<String> getColors() {
        List<String> colors = new ArrayList<>();

        return colors;
    }

    public Scoreboard build() {
        List<String> lines = this.getAllLines();
        int lineCount = lines.size();

        /* for (String str : this.scoreboard.getEntries()) {
            boolean reset = true;

            for (String score : lines) {
                if(score.equals(str)) {
                    reset = false;
                    break;
                }
            }

            if(reset) this.scoreboard.resetScores(str);
        } */

        for (Team team : this.scoreboard.getTeams()) {
            int count = StringUtil.countMatches(team.getName(), ChatColor.RED.toString());

            if(count > lineCount) team.unregister();
        }

        for (int i = 0; i < lineCount; i++) {
            String line = lines.get(i);
            int value = lineCount - 1 - i;

            String teamId = new String(new char[value + 1]).replace("\0", ChatColor.RED.toString());

            Team team = this.scoreboard.getTeam(teamId);

            if(team == null) {
                team = this.scoreboard.registerNewTeam(teamId);
                team.addEntry(teamId);

                this.objective.getScore(teamId).setScore(value);
            }

            if(!team.getPrefix().equals(line)) team.setPrefix(line);
        }

        return this.scoreboard;
    }
}
