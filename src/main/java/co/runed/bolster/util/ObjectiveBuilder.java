package co.runed.bolster.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;

public class ObjectiveBuilder {
    Scoreboard scoreboard;
    Objective objective;
    List<Score> scores = new ArrayList<>();

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
            this.scoreboard.getObjective(id).unregister();
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

    public ObjectiveBuilder addLine(String line) {
        Score score = this.objective.getScore(line);

        this.scores.add(score);

        return this;
    }

    public Scoreboard build() {
        int size = this.scores.size();
        for (int i = 0; i < size; i++) {
            Score score = this.scores.get(i);

            score.setScore(size - 1 - i);
        }

        return this.scoreboard;
    }
}
