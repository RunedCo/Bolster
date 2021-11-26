package co.runed.bolster.match;

import co.runed.bolster.game.GameMode;
import co.runed.bolster.util.TimeUtil;
import co.runed.dayroom.gson.GsonUtil;
import co.runed.dayroom.gson.JsonExclude;
import co.runed.dayroom.redis.RedisChannels;
import co.runed.dayroom.redis.RedisManager;
import co.runed.dayroom.redis.request.EndMatchPayload;
import co.runed.dayroom.redis.request.UpdateMatchHistoryPayload;
import com.google.gson.Gson;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MatchHistory {
    private static final Gson _json = GsonUtil.create();

    @JsonExclude
    private boolean started = false;

    private UUID matchId = null;
    private String gamemodeId;

    private ZonedDateTime startTime;
    private ZonedDateTime endTime;

    private List<MatchHistoryEvent> events = new ArrayList<>();

    @JsonExclude
    private String previousJson = null;

    public MatchHistory(GameMode gameMode) {
        this.gamemodeId = gameMode.getId();
    }

    public void setMatchId(UUID matchId) {
        this.matchId = matchId;
    }

    public UUID getId() {
        return matchId;
    }

    public void addEvent(MatchHistoryEvent event) {
        this.events.add(event);
    }

    public void start() {
        started = true;
        startTime = TimeUtil.now();
    }

    public boolean isStarted() {
        return started;
    }

    public void save() {
        if (matchId == null || !started) return;

        var payload = new UpdateMatchHistoryPayload();

        for (var event : events) {
            try {
                var json = _json.toJson(event);
            }
            catch (Exception e) {
                System.out.println("Error serializing evt " + event.getId());
                e.printStackTrace();
            }
        }

        payload.matchId = matchId;
        payload.json = _json.toJson(this);

        if (payload.json.equals(previousJson)) return;

        previousJson = payload.json;

        RedisManager.getInstance().publish(RedisChannels.UPDATE_MATCH_HISTORY, payload);
    }

    public void end() {
        endTime = TimeUtil.now();
        started = false;

        save();

        RedisManager.getInstance().publish(RedisChannels.END_MATCH, new EndMatchPayload(matchId));
    }
}
