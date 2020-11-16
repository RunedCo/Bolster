package co.runed.bolster.shop;

import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Shop
{
    String name;

    ArrayList<ShopItem> items = new ArrayList<>();

    public Shop(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void addItem(ShopItem item)
    {
        this.items.add(item);
    }

    public boolean isUnlocked(Player player, String id)
    {
        return true;
    }
}
