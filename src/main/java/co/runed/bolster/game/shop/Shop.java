package co.runed.bolster.game.shop;

import co.runed.bolster.managers.PlayerManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Shop
{
    String id;
    String name;

    Map<String, ShopItem> items = new HashMap<>();

    public Shop(String id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
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

    public int getNormalCost(String id)
    {
        ShopItem item = this.getItem(id);

        if (item == null) return -1;

        return item.getNormalCost();
    }

    public int getPremiumCost(String id)
    {
        ShopItem item = this.getItem(id);

        if (item == null) return -1;

        return item.getPremiumCost();
    }

    public boolean isUnlocked(Player player, String id)
    {
        return PlayerManager.getInstance().getPlayerData(player).isShopItemUnlocked(this.getId(), id);
    }

    public void unlockItem(Player player, String id)
    {
        PlayerManager.getInstance().getPlayerData(player).unlockShopItem(this.getId(), id);
    }
}
