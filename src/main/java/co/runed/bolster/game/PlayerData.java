package co.runed.bolster.game;

import co.runed.bolster.Permissions;
import co.runed.bolster.common.gson.GsonUtil;
import co.runed.bolster.common.gson.JsonExclude;
import co.runed.bolster.fx.particles.ParticleSet;
import co.runed.bolster.game.currency.Currency;
import co.runed.bolster.managers.CooldownManager;
import co.runed.bolster.managers.PlayerManager;
import co.runed.bolster.util.TimeUtil;
import co.runed.bolster.util.properties.Property;
import co.runed.bolster.util.registries.Registries;
import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.ZonedDateTime;
import java.time.temporal.TemporalAmount;
import java.util.*;

public class PlayerData
{
    //ObjectId _id;
    String name;
    UUID uuid;

    public String activeParticleSet;

    ZonedDateTime premiumExpiryTime = null;

    HashMap<String, Integer> currencies = new HashMap<>();
    HashMap<String, Integer> itemLevels = new HashMap<>();
    HashMap<String, Object> settings = new HashMap<>();
    HashMap<String, List<String>> shopUnlocks = new HashMap<>();
    List<CooldownManager.CooldownData> globalCooldowns = new ArrayList<>();

    HashMap<String, Map> gameModeData = new HashMap<>();
    @JsonExclude
    HashMap<String, GameModeData> internalGameModeData = new HashMap<>();

    @JsonExclude
    private Gson gson = GsonUtil.create();

    public void setName(String name)
    {
        this.name = name;
    }

    public void setUuid(UUID uuid)
    {
        this.uuid = uuid;
    }

    public UUID getUuid()
    {
        return uuid;
    }

    public Player getPlayer()
    {
        return Bukkit.getPlayer(this.getUuid());
    }

    public int getCurrency(Currency currency)
    {
        if (!this.currencies.containsKey(currency.getId())) return 0;

        return this.currencies.get(currency.getId());
    }

    public void setCurrency(Currency currency, int value)
    {
        this.currencies.put(currency.getId(), value);
    }

    public void addCurrency(Currency currency, int value)
    {
        this.setCurrency(currency, this.getCurrency(currency) + value);
    }

    public int getTotalItemLevel()
    {
        int level = 0;

        for (int value : this.itemLevels.values())
        {
            level += value;
        }

        return level;
    }

    public int getItemLevel(String id)
    {
        if (this.itemLevels.containsKey(id)) return this.itemLevels.get(id);

        return 0;
    }

    public void setItemLevel(String id, int level)
    {
        this.itemLevels.put(id, level);
    }

    public ParticleSet getActiveParticleSet()
    {
        return this.activeParticleSet != null ? Registries.PARTICLE_SETS.get(this.activeParticleSet) : new ParticleSet();
    }

    public void setActiveParticleSet(String id)
    {
        this.activeParticleSet = id;
    }

    public <T> T getSetting(Property<T> setting)
    {
        T value;

        try
        {
            if (!this.settings.containsKey(setting.getId())) throw new Exception();

            value = (T) this.settings.get(setting.getId());
        }
        catch (Exception e)
        {
            value = setting.getDefault();
        }

        return value;
    }

    public <T> void setSetting(Property<T> setting, T value)
    {
        this.settings.put(setting.getId(), value);
    }

    public List<String> getUnlockedShopItems(String shopId)
    {
        if (!shopUnlocks.containsKey(shopId)) return new ArrayList<>();

        return shopUnlocks.get(shopId);
    }

    public void setShopItemUnlocked(String shopId, String itemId, boolean unlocked)
    {
        if (!shopUnlocks.containsKey(shopId)) shopUnlocks.put(shopId, new ArrayList<>());

        if (unlocked)
        {
            if (!shopUnlocks.get(shopId).contains(itemId)) shopUnlocks.get(shopId).add(itemId);
        }
        else
        {
            shopUnlocks.get(shopId).remove(itemId);
        }
    }

    public boolean isShopItemUnlocked(String shopId, String itemId)
    {
        return this.getUnlockedShopItems(shopId).contains(itemId);
    }

    public boolean isPremium()
    {
        if (this.getPlayer().hasPermission(Permissions.PREMIUM)) return true;

        return getPremiumExpiryTime().isAfter(TimeUtil.now());
    }

    public ZonedDateTime getPremiumExpiryTime()
    {
//        if (this.getPlayer().hasPermission("bolster.premium")) return Instant.now().plus(Duration.ofDays(365));

        if (premiumExpiryTime == null || premiumExpiryTime.isBefore(TimeUtil.now()))
        {
            return TimeUtil.now();
        }

        return premiumExpiryTime;
    }

    public void addPremiumExpiryTime(TemporalAmount duration)
    {
        if (this.premiumExpiryTime == null)
        {
            this.premiumExpiryTime = TimeUtil.now();
        }

        this.premiumExpiryTime = this.premiumExpiryTime.plus(duration);

        if (this.premiumExpiryTime.isBefore(TimeUtil.now()))
        {
            this.premiumExpiryTime = TimeUtil.now();
        }
    }

    public void setPremiumExpiryTime(ZonedDateTime date)
    {
        this.premiumExpiryTime = date;
    }

    public List<CooldownManager.CooldownData> getGlobalCooldowns()
    {
        return globalCooldowns;
    }

    public void setGlobalCooldowns(List<CooldownManager.CooldownData> globalCooldowns)
    {
        this.globalCooldowns = globalCooldowns;
    }

    public <T extends GameModeData> T getGameModeData(Class<? extends GameMode> gameMode)
    {
        return this.getGameModeData(Registries.GAME_MODES.getId(gameMode));
    }

    // This function is just the worst
    // GameModeData is initially parsed as a Map and then that value is attempted to be parsed into the correct GameModeData
    // There is an internal hashmap of all the successfully parsed GameModeData objects that is then parsed into a map before saving
    // Like I said, it sucks...
    public <T extends GameModeData> T getGameModeData(String id)
    {
        Class<? extends GameModeData> tClass = PlayerManager.getInstance().getGameModeDataClass(id);

        if (internalGameModeData.containsKey(id))
        {
            return (T) internalGameModeData.get(id);
        }

        T value;

        if (!gameModeData.containsKey(id))
        {
            try
            {
                value = (T) tClass.getConstructor().newInstance();
            }
            catch (Exception e)
            {
                value = null;
            }
        }
        else
        {
            try
            {
                value = (T) this.gson.fromJson(this.gson.toJson(gameModeData.get(id)), tClass);
            }
            catch (Exception e)
            {
                value = null;
            }
        }

        setGameModeData(id, value);

        return value;
    }

    public <T extends GameModeData> void setGameModeData(Class<? extends GameMode> gameMode, T data)
    {
        setGameModeData(Registries.GAME_MODES.getId(gameMode), data);
    }

    public <T extends GameModeData> void setGameModeData(String id, T data)
    {
        try
        {
            internalGameModeData.put(id, data);
        }
        catch (Exception e)
        {

        }

        gameModeData.put(id, this.gson.fromJson(this.gson.toJson(data), Map.class));
    }

    public void saveGameModeData()
    {
        for (Map.Entry<String, GameModeData> data : internalGameModeData.entrySet())
        {
            gameModeData.put(data.getKey(), this.gson.fromJson(this.gson.toJson(data.getValue()), Map.class));
        }
    }
}
