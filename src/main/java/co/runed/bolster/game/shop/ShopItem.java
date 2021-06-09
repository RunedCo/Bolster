package co.runed.bolster.game.shop;

import co.runed.bolster.game.PlayerData;
import co.runed.bolster.game.currency.Currency;
import co.runed.bolster.gui.Gui;
import co.runed.bolster.gui.GuiConfirm;
import co.runed.bolster.gui.GuiConstants;
import co.runed.bolster.managers.PlayerManager;
import co.runed.bolster.util.ItemBuilder;
import co.runed.bolster.util.StringUtil;
import co.runed.bolster.util.registries.IRegisterable;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopItem implements IRegisterable
{
    String id;
    String name;
    String description = null;
    ItemStack icon;
    boolean unlockable = false;
    boolean shouldConfirm = false;
    boolean enabled = true;
    Shop parentShop;

    Map<Currency, Integer> buyCosts = new HashMap<>();
    Map<Currency, Integer> sellCosts = new HashMap<>();

    public ShopItem(String id, String name, ItemStack icon)
    {
        this.id = id;
        this.name = name;
        this.icon = icon;
    }

    public void loadFromConfig(ConfigurationSection config)
    {
        if (config.isList("sell-costs"))
        {
            this.setSellCosts(Currency.fromList(config.getStringList("sell-costs")));
        }

        if (config.isList("buy-costs"))
        {
            this.setBuyCosts(Currency.fromList(config.getStringList("buy-costs")));
        }

        this.setEnabled(config.getBoolean("enabled", this.isEnabled()));
        this.setShouldConfirm(config.getBoolean("confirm", this.shouldConfirm()));
        this.setUnlockable(config.getBoolean("unlockable", this.isUnlockable()));
        this.setDescription(config.getString("description", this.getDescription()));
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

        if (this.getDescription() != null)
        {
            builder = builder.addLore(this.getDescription());
        }

        return builder.build();
    }

    public ItemStack getIcon(Player player)
    {
        return this.getIcon();
    }

    public List<String> getShopTooltip(Player player)
    {
        List<String> shopTooltip = new ArrayList<>();

        if (!isUnlockable() || !isUnlocked(player))
        {
            shopTooltip.add("");
            shopTooltip.add(ChatColor.GRAY + "Buy For:");

            for (Map.Entry<Currency, Integer> entry : this.getBuyCosts().entrySet())
            {
                Currency currency = entry.getKey();
                String costName = currency.getName() + (currency.shouldPluralize() ? "s" : "");

                shopTooltip.addAll(StringUtil.formatBullet(ChatColor.GOLD + (entry.getValue() + " " + costName)));
            }
        }

        if (isSellable())
        {
            shopTooltip.add("");
            shopTooltip.add(ChatColor.GRAY + "Sell For:");

            for (Map.Entry<Currency, Integer> entry : this.getSellCosts().entrySet())
            {
                Currency currency = entry.getKey();
                shopTooltip.addAll(StringUtil.formatBullet(ChatColor.GOLD + (entry.getValue() + " " + currency.getPluralisedName())));
            }
        }

        return shopTooltip;
    }

    public List<String> getLeftClickTooltip(Player player)
    {
        List<String> tooltip = new ArrayList<>();

        if (isUnlockable() && isUnlocked(player))
        {
            tooltip.add(GuiConstants.UNLOCKED);
        }
        else if (canAfford(player))
        {
            tooltip.add(GuiConstants.CLICK_TO + "buy");
        }
        else
        {
            tooltip.add(GuiConstants.CANNOT_AFFORD_TO + "this!");
        }

        return tooltip;
    }

    public List<String> getRightClickTooltip(Player player)
    {
        List<String> tooltip = new ArrayList<>();

        if (isSellable() && canSell(player))
        {
            tooltip.add(GuiConstants.RIGHT_CLICK_TO + "sell");
        }
        else
        {
            tooltip.add(ChatColor.RED + "You cannot sell this!");
        }

        return tooltip;
    }

    public void setParentShop(Shop parentShop)
    {
        this.parentShop = parentShop;
    }

    public Shop getParentShop()
    {
        return parentShop;
    }

    public boolean isUnlockable()
    {
        return unlockable;
    }

    public void setUnlockable(boolean unlockable)
    {
        this.unlockable = unlockable;
    }

    public void setBuyCosts(Map<Currency, Integer> buyCosts)
    {
        this.buyCosts = buyCosts;
    }

    public void addBuyCost(Currency currency, int cost)
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

    public void setSellCosts(Map<Currency, Integer> sellCosts)
    {
        this.sellCosts = sellCosts;
    }

    public void addSellCost(Currency currency, int cost)
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

    public boolean isSellable()
    {
        return this.getSellCosts().size() > 0;
    }

    public boolean canSell(Player player)
    {
        return true;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public void onLeftClick(Gui gui, Player player)
    {
        if (isUnlockable() && isUnlocked(player))
        {
            player.sendMessage(ChatColor.RED + "You already own this!");
        }
        else if (canAfford(player))
        {
            if (shouldConfirm())
            {
                new GuiConfirm(gui, getIcon(), () -> buy(player)).show(player);
            }
            else
            {
                buy(player);
                gui.show(player);
            }
        }
        else
        {
            player.sendMessage(ChatColor.RED + "You cannot afford this!");
        }
    }

    public void onRightClick(Gui gui, Player player)
    {
        if (isUnlockable() && !isUnlocked(player))
        {
            player.sendMessage(ChatColor.RED + "You don't own this!");
        }
        else if (isSellable() && canSell(player))
        {
            if (shouldConfirm())
            {
                new GuiConfirm(gui, getIcon(), () -> sell(player)).show(player);
            }
            else
            {
                sell(player);
                gui.show(player);
            }
        }
        else
        {
            player.sendMessage(ChatColor.RED + "You cannot sell this!");
        }
    }

    public void unlock(Player player)
    {
        PlayerData playerData = PlayerManager.getInstance().getPlayerData(player);

        if (isUnlockable())
        {
            playerData.setShopItemUnlocked(this.getParentShop().getId(), id, true);
        }
    }

    public void buy(Player player)
    {
        player.sendMessage(ChatColor.GREEN + (isUnlockable() ? "Unlocked" : "Bought") + " " + this.getName());
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1, 1);

        PlayerData playerData = PlayerManager.getInstance().getPlayerData(player);

        unlock(player);

        for (Map.Entry<Currency, Integer> entry : this.getBuyCosts().entrySet())
        {
            playerData.addCurrency(entry.getKey(), -entry.getValue());
        }
    }

    public void sell(Player player)
    {
        player.sendMessage(ChatColor.GREEN + "Sold " + this.getName());

        PlayerData playerData = PlayerManager.getInstance().getPlayerData(player);

        if (isUnlockable())
        {
            playerData.setShopItemUnlocked(this.getParentShop().getId(), id, false);
        }

        for (Map.Entry<Currency, Integer> entry : this.getSellCosts().entrySet())
        {
            playerData.addCurrency(entry.getKey(), entry.getValue());
        }
    }
}
