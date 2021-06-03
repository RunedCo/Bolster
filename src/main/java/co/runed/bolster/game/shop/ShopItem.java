package co.runed.bolster.game.shop;

import co.runed.bolster.fx.Glyphs;
import co.runed.bolster.game.PlayerData;
import co.runed.bolster.game.currency.Currency;
import co.runed.bolster.gui.GuiMilestones;
import co.runed.bolster.managers.PlayerManager;
import co.runed.bolster.util.ItemBuilder;
import co.runed.bolster.util.StringUtil;
import co.runed.bolster.util.registries.IRegisterable;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ShopItem implements IRegisterable
{
    String id;
    String name;
    String description = null;
    ItemStack icon;
    boolean unlockable = false;
    boolean shouldConfirm = false;
    Shop parentShop;

    Map<Currency, Integer> buyCosts = new HashMap<>();
    Map<Currency, Integer> sellCosts = new HashMap<>();

    Consumer<Player> onPurchase = null;
    Consumer<Player> onSell = null;

    public ShopItem(String id, String name, ItemStack icon)
    {
        this.id = id;
        this.name = name;
        this.icon = icon;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    @Override
    public String getDescription()
    {
        return this.description;
    }

    @Override
    public String getId()
    {
        return id;
    }

    @Override
    public ItemStack getIcon()
    {
        ItemBuilder builder = new ItemBuilder(icon);

        builder = builder.setLore((List<String>) null);

        if (this.getDescription() != null)
        {
            builder = builder.addLore(this.getDescription());
        }

        builder = builder.addLore(this.getShopTooltip());

        return builder.build();
    }

    public List<String> getShopTooltip()
    {
        List<String> shopTooltip = new ArrayList<>();

        shopTooltip.add("");
        shopTooltip.add(ChatColor.GRAY + "Buy Cost:");

        for (Map.Entry<Currency, Integer> entry : this.getBuyCosts().entrySet())
        {
            Currency currency = entry.getKey();
            String costName = currency.getName() + (currency.shouldPluralize() ? "s" : "");

            shopTooltip.addAll(StringUtil.formatBullet(ChatColor.GOLD + (entry.getValue() + " " + costName)));
        }

        if (canSell())
        {
            shopTooltip.add("");
            shopTooltip.add(ChatColor.GRAY + "Sell Price:");

            for (Map.Entry<Currency, Integer> entry : this.getSellCosts().entrySet())
            {
                Currency currency = entry.getKey();
                shopTooltip.addAll(StringUtil.formatBullet(ChatColor.GOLD + (entry.getValue() + " " + currency.getPluralisedName())));
            }
        }

        return shopTooltip;
    }

    public void setParentShop(Shop parentShop)
    {
        this.parentShop = parentShop;
    }

    public Shop getParentShop()
    {
        return parentShop;
    }

    public void onSell(Consumer<Player> onSell)
    {
        this.onSell = onSell;
    }

    public void onPurchase(Consumer<Player> onPurchase)
    {
        this.onPurchase = onPurchase;
    }

    public boolean isUnlockable()
    {
        return unlockable;
    }

    public void setUnlockable(boolean unlockable)
    {
        this.unlockable = unlockable;
    }

    public void setBuyCost(Currency currency, int cost)
    {
        this.buyCosts.put(currency, cost);
    }

    public int getBuyCost(Currency currency)
    {
        if (!buyCosts.containsKey(currency)) return -1;

        return buyCosts.get(currency);
    }

    public Map<Currency, Integer> getBuyCosts()
    {
        return buyCosts;
    }

    public void setSellCost(Currency currency, int cost)
    {
        this.sellCosts.put(currency, cost);
    }

    public int getSellCost(Currency currency)
    {
        if (!sellCosts.containsKey(currency)) return -1;

        return sellCosts.get(currency);
    }

    public Map<Currency, Integer> getSellCosts()
    {
        return sellCosts;
    }

    public void setShouldConfirm(boolean shouldConfirm)
    {
        this.shouldConfirm = shouldConfirm;
    }

    public boolean shouldConfirm()
    {
        return shouldConfirm;
    }

    public boolean isUnlocked(Player player)
    {
        return isUnlockable() && PlayerManager.getInstance().getPlayerData(player).isShopItemUnlocked(this.getParentShop().getId(), id);
    }

    public boolean canAfford(Player player)
    {
        for (Map.Entry<Currency, Integer> entry : this.getBuyCosts().entrySet())
        {
            boolean canAfford = PlayerManager.getInstance().getPlayerData(player).getCurrency(entry.getKey()) >= entry.getValue();

            if (!canAfford) return false;
        }

        return true;
    }

    public boolean canSell()
    {
        return this.getSellCosts().size() > 0;
    }

    public void buy(Player player)
    {
        PlayerData playerData = PlayerManager.getInstance().getPlayerData(player);

        if (isUnlockable())
        {
            playerData.setShopItemUnlocked(this.getId(), id, true);
        }

        for (Map.Entry<Currency, Integer> entry : this.getBuyCosts().entrySet())
        {
            playerData.addCurrency(entry.getKey(), -entry.getValue());
        }

        if (this.onPurchase == null) return;

        this.onPurchase.accept(player);
    }

    public void sell(Player player)
    {
        PlayerData playerData = PlayerManager.getInstance().getPlayerData(player);

        if (isUnlockable())
        {
            playerData.setShopItemUnlocked(this.getId(), id, false);
        }

        for (Map.Entry<Currency, Integer> entry : this.getSellCosts().entrySet())
        {
            playerData.addCurrency(entry.getKey(), entry.getValue());
        }

        if (this.onSell == null) return;

        this.onSell.accept(player);
    }
}
