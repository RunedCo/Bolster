package co.runed.bolster.registries;

import co.runed.bolster.items.Item;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

public class ItemRegistry extends Registry<Item> {
    private final HashMap<String, Class<? extends Item>> fileItems = new HashMap<>();

    public ItemRegistry(Plugin plugin) {
        super(plugin);
    }

    @Override
    public boolean contains(String id) {
        return super.contains(id) || this.fileItems.containsKey(id);
    }

    @Override
    public Item createInstance(String id) {
        if(super.contains(id)) {
            Item item = super.createInstance(id);

            if (item == null) return null;

            item.setId(id);

            return item;
        }

        // LOAD FROM FILE

        return null;
    }
}
