package co.runed.bolster.game.shop;

import co.runed.bolster.game.currency.Currency;
import co.runed.bolster.managers.PlayerManager;
import org.bukkit.entity.Player;

public class CurrencyShopItem extends ShopItem {
    Currency currency;

    public CurrencyShopItem(Currency currency) {
        super(currency.getId(), currency.getName(), currency.getIcon());

        this.currency = currency;
    }

    @Override
    public boolean canSell(Player player) {
        var playerData = PlayerManager.getInstance().getPlayerData(player);

        return playerData.getCurrency(currency) >= 1;
    }

    @Override
    public void buy(Player player) {
        super.buy(player);

        var playerData = PlayerManager.getInstance().getPlayerData(player);

        playerData.addCurrency(currency, 1);
    }

    @Override
    public void sell(Player player) {
        super.sell(player);

        var playerData = PlayerManager.getInstance().getPlayerData(player);

        playerData.addCurrency(currency, -1);
    }
}
