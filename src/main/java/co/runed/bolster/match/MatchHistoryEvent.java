package co.runed.bolster.match;

import co.runed.bolster.common.util.Identifiable;
import co.runed.bolster.util.TimeUtil;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

public abstract class MatchHistoryEvent implements Identifiable {
    String id;
    ZonedDateTime timestamp;
    Map<String, Object> extraData = new HashMap<>();

    public MatchHistoryEvent(String id) {
        this.id = id;
        this.timestamp = TimeUtil.now();
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
