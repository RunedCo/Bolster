package co.runed.bolster.game.shop;

import co.runed.bolster.Bolster;
import co.runed.dayroom.util.Identifiable;
import co.runed.dayroom.util.Nameable;
import co.runed.bolster.events.player.SavePlayerDataEvent;
import co.runed.bolster.game.currency.Currency;
import co.runed.bolster.util.config.Configurable;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Shop implements Identifiable, Configurable, Nameable, Listener {
    String id;
    String name;

    Map<String, ShopItem> items = new HashMap<>();

    public Shop(String id, String name) {
        this.id = id;
        this.name = name;

        Bukkit.getPluginManager().registerEvents(this, Bolster.getInstance());
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void addItem(ShopItem item) {
        item.setParentShop(this);

        this.items.put(item.getId(), item);
    }

    public ShopItem getItem(String id) {
        if (!this.items.containsKey(id)) return null;

        return this.items.get(id);
    }

    public Map<String, ShopItem> getItems() {
        return items;
    }

    public int getBuyCost(Currency currency, String id) {
        var item = this.getItem(id);

        if (item == null) return -1;

        return item.getBuyCost(currency);
    }

    public int getSellCost(Currency currency, String id) {
        var item = this.getItem(id);

        if (item == null) return -1;

        return item.getSellCost(currency);
    }

    public boolean isUnlocked(Player player, String id) {
        return this.items.get(id).isUnlocked(player);
    }

    public boolean isUnlocked(UUID uuid, String id) {
        return this.items.get(id).isUnlocked(uuid);
    }

    @Override
    public void loadConfig(ConfigurationSection config) {
        for (var key : config.getKeys(false)) {
            if (this.getItem(key) == null) continue;
            if (!config.isConfigurationSection(key)) continue;

            var item = this.getItem(key);
            var sec = config.getConfigurationSection(key);

            item.loadFromConfig(sec);
        }
    }

    @EventHandler
    private void onSavePlayer(SavePlayerDataEvent event) {
        var playerData = event.getPlayerData();

        for (var item : this.getItems().keySet()) {
            if (!this.isUnlocked(playerData.getUuid(), item)) {
                playerData.setShopItemUnlocked(this.getId(), item, false);
            }
        }
    }
}
