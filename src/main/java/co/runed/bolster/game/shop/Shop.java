package co.runed.bolster.game.shop;

import co.runed.bolster.game.currency.Currency;
import co.runed.bolster.managers.PlayerManager;
import co.runed.bolster.util.registries.IRegisterable;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class Shop implements IRegisterable
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

    @Override
    public String getDescription()
    {
        return null;
    }

    public void addItem(ShopItem item)
    {
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
}
