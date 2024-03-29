package co.runed.bolster.match;

import co.runed.bolster.util.TimeUtil;
import co.runed.dayroom.util.Identifiable;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

public abstract class MatchHistoryEvent implements Identifiable {
    private String id;
    private ZonedDateTime timestamp = TimeUtil.now();
    private Map<String, Object> extraData = new HashMap<>();

    public MatchHistoryEvent(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    public void addExtraData(String key, Object data) {
        this.extraData.put(key, data);
    }

    public Map<String, Object> getExtraData() {
        return extraData;
    }
}
