package co.runed.bolster;

import co.runed.bolster.items.Item;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.concurrent.Callable;

public class ItemRegistry {
    private HashMap<String, Callable<? extends Item>> items = new HashMap<>();

    public ItemRegistry(Plugin plugin) {

    }

    public void register(String id, Callable<? extends Item> itemFunc) {
        this.items.putIfAbsent(id, itemFunc);
    }

    public Item createItemInstance(String id) {
        if (!this.items.containsKey(id)) return null;

        try {
            Item item = this.items.get(id).call();

            item.setId(id);

            return item;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
