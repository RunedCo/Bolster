package co.runed.bolster;

import co.runed.bolster.wip.Currency;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.UUID;

public class PlayerData
{
    @SerializedName("uuid")
    UUID uuid;
    HashMap<String, Integer> currencies = new HashMap<>();

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
}
