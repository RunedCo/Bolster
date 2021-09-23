package co.runed.bolster.match;

import co.runed.dayroom.gson.JsonExclude;
import co.runed.bolster.events.game.MatchHistoryAddedEvent;
import co.runed.bolster.game.GameMode;
import co.runed.bolster.util.BukkitUtil;
import co.runed.bolster.util.TimeUtil;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MatchHistory {
    @JsonExclude
    boolean started = false;

    UUID matchId = UUID.randomUUID();
    String gamemodeId;

    ZonedDateTime startTime;
    ZonedDateTime endTime;

    List<MatchHistoryEvent> events = new ArrayList<>();

    public MatchHistory(GameMode gameMode) {
        this.gamemodeId = gameMode.getId();
    }

    public void addEvent(MatchHistoryEvent event) {
        var bukkitEvent = BukkitUtil.triggerEvent(new MatchHistoryAddedEvent(this, event));

        if (bukkitEvent.isCancelled()) return;

        this.events.add(event);
    }

    public void start() {
        started = true;
        startTime = TimeUtil.now();
    }

    public boolean isStarted() {
        return started;
    }

    public void end() {
        endTime = TimeUtil.now();
        started = false;
    }
}
