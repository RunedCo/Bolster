package co.runed.bolster.util;

import co.runed.bolster.wip.Currency;
import co.runed.bolster.wip.settings.Setting;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerData
{
    //ObjectId _id;
    UUID uuid;

    HashMap<String, Integer> currencies = new HashMap<>();
    HashMap<String, Integer> itemLevels = new HashMap<>();
    HashMap<String, Object> settings = new HashMap<>();

    public void onSave()
    {

    }

    public void onLoad()
    {

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

    public <T> T getSetting(Setting<T> setting)
    {
        T value;

        try
        {
            value = (T) this.settings.get(setting.getId());
        }
        catch (Exception e)
        {
            value = setting.getDefault();
        }

        return value;
    }

    public <T> void setSetting(Setting<T> setting, T value)
    {
        this.settings.put(setting.getId(), value);
    }
}
