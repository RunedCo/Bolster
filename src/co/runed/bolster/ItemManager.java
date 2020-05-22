package co.runed.bolster;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.BowProjectileAbility;
import co.runed.bolster.items.BowItem;
import co.runed.bolster.items.Item;
import co.runed.bolster.items.ItemAbilitySlot;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
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
        item = Bolster.getItemRegistry().createInstance(id);

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

    public boolean isPlayerHolding(Player player, Item item) {
        PlayerInventory inv = player.getInventory();
        ItemStack stack = inv.getItemInMainHand();
        String itemId = this.getItemIdFromStack(stack);

        if (item.getOwner() == null || player != item.getOwner()) return false;

        return itemId.equals(item.getId());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
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

    @EventHandler
    public void onPlayerShootBow(EntityShootBowEvent e) {
        if(e.getEntityType() != EntityType.PLAYER) return;

        Player player = (Player)e.getEntity();
        ItemStack stack = e.getBow();
        String itemId = this.getItemIdFromStack(stack);

        if(itemId == null) return;

        Item item = this.getOrCreateItem(player, itemId);

        if(item == null) return;
        if(item.getOwner() == null || player != item.getOwner()) return;
        if(!(item instanceof BowItem)) return;

        BowItem bowItem = (BowItem) item;
        Ability ability = bowItem.getAbility(ItemAbilitySlot.SHOOT);

        // TODO: MAKE LESS HACKY
        // TODO: MAYBE ADD PARAMETERS TO CAST ABILITY
        if(ability instanceof BowProjectileAbility) {
            BowProjectileAbility projAbility = (BowProjectileAbility)ability;

            projAbility.setForce(e.getForce());
            projAbility.setVelocity(e.getProjectile().getVelocity());

            bowItem.setAbility(projAbility, ItemAbilitySlot.SHOOT);
        }

        bowItem.castAbility(ItemAbilitySlot.SHOOT);

        e.setCancelled(true);
    }
}
