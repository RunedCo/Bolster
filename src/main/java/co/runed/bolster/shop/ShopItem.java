package co.runed.bolster.shop;

import org.bukkit.entity.Player;

public abstract class ShopItem
{
    final int normalCost;
    final int premiumCost;
    String id;

    public ShopItem(String id, int normalCost, int premiumCost)
    {
        this.id = id;
        this.normalCost = normalCost;
        this.premiumCost = premiumCost;
    }

    public String getName()
    {
        return "Shop Item";
    }

    public String getId()
    {
        return id;
    }

    public int getNormalCost()
    {
        return normalCost;
    }

    public int getPremiumCost()
    {
        return premiumCost;
    }

    public abstract boolean isOwned(Player player);

    public abstract void onPurchase(Player player);
}
