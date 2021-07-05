package co.runed.bolster.game.shop;

import co.runed.bolster.game.currency.Currency;
import co.runed.bolster.util.INameable;
import co.runed.bolster.util.config.IConfigurable;
import co.runed.bolster.util.registries.IRegisterable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class Shop implements IRegisterable, IConfigurable, INameable
{
    String id;
    String name;

    Map<String, ShopItem> items = new HashMap<>();

    public Shop(String id, String name)
    {
        this.id = id;
        this.name = name;
    }

    @Override
    public String getId()
    {
        return id;
    }

    @Override
    public String getName()
    {
        return name;
    }

    public void addItem(ShopItem item)
    {
        item.setParentShop(this);

        this.items.put(item.getId(), item);
    }

    public ShopItem getItem(String id)
    {
        if (!this.items.containsKey(id)) return null;

        return this.items.get(id);
    }

    public Map<String, ShopItem> getItems()
    {
        return items;
    }

    public int getBuyCost(Currency currency, String id)
    {
        ShopItem item = this.getItem(id);

        if (item == null) return -1;

        return item.getBuyCost(currency);
    }

    public int getSellCost(Currency currency, String id)
    {
        ShopItem item = this.getItem(id);

        if (item == null) return -1;

        return item.getSellCost(currency);
    }

    public boolean isUnlocked(Player player, String id)
    {
        return this.items.get(id).isUnlocked(player);
    }

    @Override
    public void loadConfig(ConfigurationSection config)
    {
        for (String key : config.getKeys(false))
        {
            if (this.getItem(key) == null) continue;
            if (!config.isConfigurationSection(key)) continue;

            ShopItem item = this.getItem(key);
            ConfigurationSection sec = config.getConfigurationSection(key);

            item.loadFromConfig(sec);
        }
    }

    @Override
    public void create()
    {
        
    }
}
