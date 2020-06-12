package co.runed.bolster.managers;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.properties.AbilityProperties;
import co.runed.bolster.items.Item;
import co.runed.bolster.items.ItemAbilitySlot;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ItemManager implements Listener {
    Plugin plugin;

    private final HashMap<UUID, List<Item>> entityItems = new HashMap<>();

    public ItemManager(Plugin plugin) {
        this.plugin = plugin;

        Bolster.getInstance().getServer().getPluginManager().registerEvents(this, plugin);
    }

    public Item getItem(LivingEntity entity, String id) {
        this.entityItems.putIfAbsent(entity.getUniqueId(), new ArrayList<>());

        List<Item> items = this.entityItems.get(entity.getUniqueId());

        for (Item item : items) {
            if(item.getId().equals(id)) return item;
        }

        return null;
    }

    public void removeItem(LivingEntity entity, Item item) {
        if(!this.entityItems.containsKey(entity.getUniqueId())) return;

        item.destroy();

        this.entityItems.get(entity.getUniqueId()).remove(item);
    }

    public void clearItems(LivingEntity entity) {
        for (Item item : this.getItems(entity)) {
            this.removeItem(entity, item);
        }
    }

    public boolean hasItem(LivingEntity entity, String id) {
        return this.getItem(entity, id) != null;
    }

    public List<Item> getItems(LivingEntity entity) {
        if(!this.entityItems.containsKey(entity.getUniqueId())) return new ArrayList<>();

        return this.entityItems.get(entity.getUniqueId());
    }

    // TODO: LOAD DATA FROM ITEMS
    public Item createItem(LivingEntity entity, String id) {
        this.entityItems.putIfAbsent(entity.getUniqueId(), new ArrayList<>());

        List<Item> items = this.entityItems.get(entity.getUniqueId());

        // CHECK IF ITEM INSTANCE ALREADY EXISTS FOR PLAYER

        Item item = this.getItem(entity, id);

        if (item != null) {
            item.setOwner(entity);
            return item;
        }

        // IF NOT CREATE NEW ONE
        item = Bolster.getItemRegistry().createInstance(id);

        if (item == null) return null;

        item.setOwner(entity);

        items.add(item);

        return item;
    }

    public String getItemIdFromStack(ItemStack stack) {
        if(stack == null) return null;
        if(!stack.hasItemMeta()) return null;

        ItemMeta meta = stack.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        return pdc.get(Item.ITEM_ID_KEY, PersistentDataType.STRING);
    }

    public boolean isEntityHolding(LivingEntity entity, Item item) {
        EntityEquipment inv = entity.getEquipment();
        ItemStack stack = inv.getItemInMainHand();
        String itemId = this.getItemIdFromStack(stack);

        if (item.getOwner() == null || entity != item.getOwner()) return false;
        if (itemId == null) return false;
        if(item.getId() == null) return false;

        return itemId.equals(item.getId());
    }

    public void reset() {
        this.entityItems.clear();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        for (ItemStack stack : player.getInventory()) {
            String itemId = this.getItemIdFromStack(stack);

            if(itemId == null) return;

            this.createItem(player, itemId);
        }
    }

    // TODO: WOULD BE GOOD FOR OPTIMIZATION TO REMOVE ALL ITEM INSTANCES BUT MAY CAUSE ISSUES WITH PLAYERS DCING MID GAME
    /*@EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        this.clearItems(player);
    }*/

    @EventHandler
    public void onPlayerDie(PlayerDeathEvent event) {
        Player player = event.getEntity();

        this.clearItems(player);

        this.entityItems.remove(player.getUniqueId());
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        String itemId = this.getItemIdFromStack(event.getItemDrop().getItemStack());

        if(itemId == null) return;

        if(this.hasItem(player, itemId)) {
            Item item = this.getItem(player, itemId);

            this.removeItem(player, item);
        }
    }

    @EventHandler
    public void onPickupItem(EntityPickupItemEvent event) {
        LivingEntity entity = event.getEntity();
        ItemStack stack = event.getItem().getItemStack();

        String itemId = this.getItemIdFromStack(stack);

        if(itemId == null) return;

        this.createItem(entity, itemId);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack stack = e.getItem();
        String itemId = this.getItemIdFromStack(stack);

        if(itemId == null) return;

        Item item = this.createItem(player, itemId);

        if(item == null) return;
        if(item.getOwner() == null || player != item.getOwner()) return;

        AbilityProperties properties = new AbilityProperties();
        properties.set(AbilityProperties.CASTER, player);
        properties.set(AbilityProperties.ITEM_STACK, stack);
        properties.set(AbilityProperties.BLOCK_ACTION, e.getAction());
        properties.set(AbilityProperties.BLOCK, e.getClickedBlock());
        properties.set(AbilityProperties.BLOCK_FACE, e.getBlockFace());

        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
            item.castAbility(ItemAbilitySlot.LEFT_CLICK, properties);
        }

        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            item.castAbility(ItemAbilitySlot.RIGHT_CLICK, properties);
        }
    }

    @EventHandler
    public void onPlayerOffhand(PlayerSwapHandItemsEvent e) {
        Player player = e.getPlayer();
        ItemStack stack = e.getOffHandItem();
        String itemId = this.getItemIdFromStack(stack);

        if(itemId == null) return;

        Item item = this.createItem(player, itemId);

        if(item == null) return;
        if(item.getOwner() == null || player != item.getOwner()) return;

        if(item.getAbility(ItemAbilitySlot.SWAP_ITEM) != null) {
            AbilityProperties properties = new AbilityProperties();
            properties.set(AbilityProperties.CASTER, player);
            properties.set(AbilityProperties.ITEM_STACK, stack);

            item.castAbility(ItemAbilitySlot.SWAP_ITEM, properties);

            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onLivingEntityShootBow(EntityShootBowEvent e) {
        LivingEntity entity = e.getEntity();
        ItemStack stack = e.getBow();
        String itemId = this.getItemIdFromStack(stack);

        if(itemId == null) return;

        Item item = this.createItem(entity, itemId);

        if(item == null) return;
        if(item.getOwner() == null || entity != item.getOwner()) return;
        if(item.getAbility(ItemAbilitySlot.ON_SHOOT) == null) return;

        AbilityProperties properties = new AbilityProperties();
        properties.set(AbilityProperties.CASTER, entity);
        properties.set(AbilityProperties.ITEM_STACK, stack);
        properties.set(AbilityProperties.FORCE, e.getForce());
        properties.set(AbilityProperties.VELOCITY, e.getProjectile().getVelocity());

        item.castAbility(ItemAbilitySlot.ON_SHOOT, properties);

        e.setCancelled(true);
    }
}
