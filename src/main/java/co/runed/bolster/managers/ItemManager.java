package co.runed.bolster.managers;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.properties.AbilityProperties;
import co.runed.bolster.items.Item;
import co.runed.bolster.items.ItemAbilitySlot;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EntityEquipment;
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

    private final HashMap<LivingEntity, List<Item>> entityItems = new HashMap<>();

    public ItemManager(Plugin plugin) {
        this.plugin = plugin;

        Bolster.getInstance().getServer().getPluginManager().registerEvents(this, plugin);
    }

    public Item getItem(LivingEntity entity, String id) {
        this.entityItems.putIfAbsent(entity, new ArrayList<>());

        List<Item> items = this.entityItems.get(entity);

        for (Item item : items) {
            if(item.getId().equals(id)) return item;
        }

        return null;
    }

    public boolean hasItem(LivingEntity entity, String id) {
        return this.getItem(entity, id) != null;
    }

    // TODO: LOAD DATA FROM ITEMS
    public Item getOrCreateItem(LivingEntity entity, String id) {
        this.entityItems.putIfAbsent(entity, new ArrayList<>());

        List<Item> items = this.entityItems.get(entity);

        // CHECK IF ITEM INSTANCE ALREADY EXISTS FOR PLAYER

        Item item = this.getItem(entity, id);

        if (item != null) return item;

        // IF NOT CREATE NEW ONE
        item = Bolster.getItemRegistry().createInstance(id);

        if (item == null) return null;

        item.setOwner(entity);

        items.add(item);

        return item;
    }

    public void reset() {
        this.entityItems.clear();
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

        return itemId.equals(item.getId());
    }

    @EventHandler
    public void onLivingEntityInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack stack = e.getItem();
        String itemId = this.getItemIdFromStack(stack);

        if(itemId == null) return;

        Item item = this.getOrCreateItem(player, itemId);

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
    public void onLivingEntityOffhand(PlayerSwapHandItemsEvent e) {
        Player player = e.getPlayer();
        ItemStack stack = e.getOffHandItem();
        String itemId = this.getItemIdFromStack(stack);

        if(itemId == null) return;

        Item item = this.getOrCreateItem(player, itemId);

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
        if(e.getEntityType() != EntityType.PLAYER) return;

        LivingEntity entity = (LivingEntity)e.getEntity();
        ItemStack stack = e.getBow();
        String itemId = this.getItemIdFromStack(stack);

        if(itemId == null) return;

        Item item = this.getOrCreateItem(entity, itemId);

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
