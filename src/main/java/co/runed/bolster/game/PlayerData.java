package co.runed.bolster.game;

import co.runed.bolster.Permissions;
import co.runed.bolster.fx.particles.ParticleSet;
import co.runed.bolster.game.currency.Currency;
import co.runed.bolster.managers.PlayerManager;
import co.runed.bolster.util.TimeUtil;
import co.runed.bolster.util.registries.Registries;
import co.runed.bolster.wip.Cooldown;
import co.runed.dayroom.gson.GsonUtil;
import co.runed.dayroom.gson.JsonExclude;
import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.ZonedDateTime;
import java.time.temporal.TemporalAmount;
import java.util.*;

public class PlayerData {
    //ObjectId _id;
    String name;
    UUID uuid;

    public String activeParticleSet;

    ZonedDateTime joinTime = TimeUtil.now();
    public ZonedDateTime lastJoinTime;
    ZonedDateTime premiumExpiryTime = TimeUtil.now();

    HashMap<String, Integer> currencies = new HashMap<>();
    HashMap<String, Integer> providerLevels = new HashMap<>();
    HashMap<String, Object> settings = new HashMap<>();
    HashMap<String, Map<String, Boolean>> shopUnlocks = new HashMap<>();
    List<Cooldown> globalCooldowns = new ArrayList<>();

    HashMap<String, Map> gameModeData = new HashMap<>();
    @JsonExclude
    HashMap<String, GameModeData> internalGameModeData = new HashMap<>();

    @JsonExclude
    private Gson gson = GsonUtil.create();

    public void setName(String name) {
        this.name = name;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(this.getUuid());
    }

    public int getCurrency(Currency currency) {
        if (!this.currencies.containsKey(currency.getId())) return 0;

        return this.currencies.get(currency.getId());
    }

    public void setCurrency(Currency currency, int value) {
        this.currencies.put(currency.getId(), value);
    }

    public void addCurrency(Currency currency, int value) {
        this.setCurrency(currency, this.getCurrency(currency) + value);
    }

    public int getTotalProviderLevels() {
        var level = 0;

        for (int value : this.providerLevels.values()) {
            level += value;
        }

        return level;
    }

    public int getProviderLevel(String id) {
        if (this.providerLevels.containsKey(id)) return this.providerLevels.get(id);

        return 0;
    }

    public void setProviderLevel(String id, int level) {
        this.providerLevels.put(id, level);
    }

    public ParticleSet getActiveParticleSet() {
        return this.activeParticleSet != null ? Registries.PARTICLE_SETS.get(this.activeParticleSet) : new ParticleSet();
    }

    public void setActiveParticleSet(String id) {
        this.activeParticleSet = id;
    }

    public <T> T getSetting(Setting<T> setting) {
        T value;

        try {
            if (!this.settings.containsKey(setting.getId())) throw new Exception();

            value = (T) this.settings.get(setting.getId());
        }
        catch (Exception e) {
            value = setting.getDefault();
        }

        return value;
    }

    public <T> void setSetting(Setting<T> setting, T value) {
        this.settings.put(setting.getId(), value);
    }

    public HashMap<String, Object> getSettings() {
        return settings;
    }

    public List<String> getUnlockedShopItems(String shopId) {
        if (!shopUnlocks.containsKey(shopId)) return new ArrayList<>();

        return shopUnlocks.get(shopId).entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();
    }

    public void setShopItemUnlocked(String shopId, String itemId, boolean unlocked) {
        if (!shopUnlocks.containsKey(shopId)) shopUnlocks.put(shopId, new HashMap<>());

        shopUnlocks.get(shopId).put(itemId, unlocked);
    }

    public boolean isShopItemUnlocked(String shopId, String itemId) {
        return this.getUnlockedShopItems(shopId).contains(itemId);
    }

    public boolean isPremium() {
        if (this.getPlayer().hasPermission(Permissions.PREMIUM)) return true;

        return getPremiumExpiryTime().isAfter(TimeUtil.now());
    }

    public ZonedDateTime getPremiumExpiryTime() {
//        if (this.getPlayer().hasPermission("bolster.premium")) return Instant.now().plus(Duration.ofDays(365));

        if (premiumExpiryTime == null || premiumExpiryTime.isBefore(TimeUtil.now())) {
            return TimeUtil.now();
        }

        return premiumExpiryTime;
    }

    public void addPremiumExpiryTime(TemporalAmount duration) {
        if (this.premiumExpiryTime == null) {
            this.premiumExpiryTime = TimeUtil.now();
        }

        this.premiumExpiryTime = this.premiumExpiryTime.plus(duration);

        if (this.premiumExpiryTime.isBefore(TimeUtil.now())) {
            this.premiumExpiryTime = TimeUtil.now();
        }
    }

    public void setPremiumExpiryTime(ZonedDateTime date) {
        this.premiumExpiryTime = date;
    }

    public List<Cooldown> getGlobalCooldowns() {
        return globalCooldowns;
    }

    public void setGlobalCooldowns(List<Cooldown> globalCooldowns) {
        this.globalCooldowns = globalCooldowns;
    }

    public <T extends GameModeData> T getGameModeData(Class<? extends GameMode> gameMode) {
        return this.getGameModeData(Registries.GAME_MODES.getId(gameMode));
    }

    // This function is just the worst
    // GameModeData is initially parsed as a Map and then that value is attempted to be parsed into the correct GameModeData
    // There is an internal hashmap of all the successfully parsed GameModeData objects that is then parsed into a map before saving
    // Like I said, it sucks...
    public <T extends GameModeData> T getGameModeData(String id) {
        var tClass = PlayerManager.getInstance().getGameModeDataClass(id);

        if (internalGameModeData.containsKey(id)) {
            return (T) internalGameModeData.get(id);
        }

        T value;

        if (!gameModeData.containsKey(id)) {
            try {
                value = (T) tClass.getConstructor().newInstance();
            }
            catch (Exception e) {
                value = null;
            }
        }
        else {
            try {
                value = (T) this.gson.fromJson(this.gson.toJson(gameModeData.get(id)), tClass);
            }
            catch (Exception e) {
                value = null;
            }
        }

        setGameModeData(id, value);

        return value;
    }

    public <T extends GameModeData> void setGameModeData(Class<? extends GameMode> gameMode, T data) {
        setGameModeData(Registries.GAME_MODES.getId(gameMode), data);
    }

    public <T extends GameModeData> void setGameModeData(String id, T data) {
        try {
            internalGameModeData.put(id, data);
        }
        catch (Exception e) {

        }

        gameModeData.put(id, this.gson.fromJson(this.gson.toJson(data), Map.class));
    }

    public void saveGameModeData() {
        for (var data : internalGameModeData.entrySet()) {
            gameModeData.put(data.getKey(), this.gson.fromJson(this.gson.toJson(data.getValue()), Map.class));
        }
    }
}
