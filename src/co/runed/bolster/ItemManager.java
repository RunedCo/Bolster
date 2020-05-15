package co.runed.bolster;

import co.runed.bolster.items.Item;
import co.runed.bolster.items.ItemAbilitySlot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemManager implements Listener {
    Plugin plugin;

    private final HashMap<Player, List<Item>> playerItems = new HashMap<>();

    public ItemManager(Plugin plugin) {
        this.plugin = plugin;

        Bolster.getInstance().getServer().getPluginManager().registerEvents(this, plugin);
    }

    public Item getItem(Player player, String id) {
        this.playerItems.putIfAbsent(player, new ArrayList<>());

        List<Item> items = this.playerItems.get(player);

        for (Item item : items) {
            if(item.getId().equals(id)) return item;
        }

        return null;
    }

    public boolean hasItem(Player player, String id) {
        return this.getItem(player, id) != null;
    }

    public Item getOrCreateItem(Player player, String id) {
        this.playerItems.putIfAbsent(player, new ArrayList<>());

        List<Item> items = this.playerItems.get(player);

        // CHECK IF ITEM INSTANCE ALREADY EXISTS FOR PLAYER

        Item item = this.getItem(player, id);

        if (item != null) return item;

        // IF NOT CREATE NEW ONE
        item = Bolster.getItemRegistry().createItemInstance(id);

        if (item == null) return null;

        item.setOwner(player);

        items.add(item);

        return item;
    }

    public void reset() {
        this.playerItems.clear();
    }

    public String getItemIdFromStack(ItemStack stack) {
        if(stack == null) return null;
        if(!stack.hasItemMeta()) return null;

        ItemMeta meta = stack.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        return pdc.get(Item.ITEM_ID_KEY, PersistentDataType.STRING);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        ItemRegistry itemRegistry = Bolster.getItemRegistry();
        Player player = e.getPlayer();
        ItemStack stack = e.getItem();
        String itemId = this.getItemIdFromStack(stack);

        if(itemId == null) return;

        Item item = this.getOrCreateItem(player, itemId);

        if(item == null) return;
        if(item.getOwner() == null || player != item.getOwner()) return;

        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
            item.castAbility(ItemAbilitySlot.LEFT);
        }

        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            item.castAbility(ItemAbilitySlot.RIGHT);
        }
    }
}
