package co.runed.bolster.managers;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.properties.AbilityProperties;
import co.runed.bolster.items.Item;
import co.runed.bolster.items.ItemAction;
import co.runed.bolster.properties.Properties;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class ItemManager implements Listener {
    Plugin plugin;

    private final HashMap<UUID, List<Item>> entityItems = new HashMap<>();

    public ItemManager(Plugin plugin) {
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     *  Gets an {@link Item} from a {@link LivingEntity}
     *
     * @param entity the entity that has the item
     * @param id     the {@link String} id of the item as registered in {@link co.runed.bolster.registries.ItemRegistry}
     * @return       the existing {@link Item} instance or null
     */
    public Item getItem(LivingEntity entity, String id) {
        this.entityItems.putIfAbsent(entity.getUniqueId(), new ArrayList<>());

        List<Item> items = this.entityItems.get(entity.getUniqueId());

        for (Item item : items) {
            if(item.getId().equals(id)) return item;
        }

        return null;
    }

    /**
     *  Creates an {@link Item} instance unless entity already has one
     *  in which case it gets the existing instance
     *
     * @param entity the entity that the item should be created for
     * @param id the {@link String} id of the item as registered in {@link co.runed.bolster.registries.ItemRegistry}
     */
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

    public void removeItem(LivingEntity entity, Item item) {
        if(!this.entityItems.containsKey(entity.getUniqueId())) return;

        item.destroy();

        this.entityItems.get(entity.getUniqueId()).remove(item);
    }

    public void clearItems(LivingEntity entity) {
        List<Item> items = new ArrayList<>(this.getItems(entity));

        for (Item item : items) {
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

            Item item = this.createItem(player, itemId);

            int slot = player.getInventory().first(stack);
            ItemStack updatedStack = item.toItemStack();
            updatedStack.setAmount(stack.getAmount());

            player.getInventory().setItem(slot, updatedStack);
        }
    }

    // TODO: WOULD BE GOOD FOR OPTIMIZATION TO REMOVE ALL ITEM INSTANCES BUT MAY CAUSE ISSUES WITH PLAYERS DCING MID GAME
    // TODO: MAYBE ADD TO 5 MIN TIMER/DELAY?
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
        ItemStack stack = event.getItemDrop().getItemStack();
        String itemId = this.getItemIdFromStack(stack);

        if(itemId == null) return;

        Item item = this.createItem(player, itemId);

        if(item == null) return;
        if(item.getOwner() == null || player != item.getOwner()) return;

        if(item.getAbility(ItemAction.ON_DROP_ITEM) != null) {
            Properties properties = new Properties();
            properties.set(AbilityProperties.CASTER, player);
            properties.set(AbilityProperties.ITEM_STACK, stack);
            properties.set(AbilityProperties.EVENT, event);

            item.castAbility(ItemAction.ON_DROP_ITEM, properties);

            event.setCancelled(true);
            return;
        }

        this.removeItem(player, item);
    }

    @EventHandler
    public void onPickupItem(EntityPickupItemEvent event) {
        LivingEntity entity = event.getEntity();
        ItemStack stack = event.getItem().getItemStack();

        String itemId = this.getItemIdFromStack(stack);

        if(itemId == null) return;

        this.createItem(entity, itemId);
    }

    /**
     *  Event that triggers casting an items ability on left or right click
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = event.getItem();
        String itemId = this.getItemIdFromStack(stack);

        if(itemId == null) return;

        Item item = this.createItem(player, itemId);

        if(item == null) return;
        if(item.getOwner() == null || player != item.getOwner()) return;

        Properties properties = new Properties();
        properties.set(AbilityProperties.CASTER, player);
        properties.set(AbilityProperties.ITEM_STACK, stack);
        properties.set(AbilityProperties.BLOCK_ACTION, event.getAction());
        properties.set(AbilityProperties.BLOCK, event.getClickedBlock());
        properties.set(AbilityProperties.BLOCK_FACE, event.getBlockFace());
        properties.set(AbilityProperties.EVENT, event);

        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            item.castAbility(ItemAction.LEFT_CLICK, properties);
        }

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            item.castAbility(ItemAction.RIGHT_CLICK, properties);
        }
    }

    /**
     *  Event that triggers casting an items ability when an entity is damaged
     */
    @EventHandler
    public void onDamageEntity(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof LivingEntity)) return;

        LivingEntity entity = (LivingEntity) event.getDamager();
        EntityEquipment inv = entity.getEquipment();
        ItemStack stack = inv.getItemInMainHand();

        String itemId = this.getItemIdFromStack(stack);

        if(itemId == null) return;

        Item item = this.createItem(entity, itemId);

        if(item == null) return;
        if(item.getOwner() == null || entity != item.getOwner()) return;

        Properties properties = new Properties();
        properties.set(AbilityProperties.CASTER, entity);
        properties.set(AbilityProperties.TARGETS, new ArrayList<>(Arrays.asList((LivingEntity) event.getEntity())));
        properties.set(AbilityProperties.ITEM_STACK, stack);
        properties.set(AbilityProperties.EVENT, event);
        properties.set(AbilityProperties.DAMAGE, event.getDamage());

        item.castAbility(ItemAction.ON_DAMAGE_ENTITY, properties);
    }

    /**
     *  Event that triggers casting an items ability when an entity is killed
     */
    @EventHandler
    public void onKillEntity(EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();

        ItemStack stack = player.getInventory().getItemInMainHand();

        String itemId = this.getItemIdFromStack(stack);

        if(itemId == null) return;

        Item item = this.createItem(player, itemId);

        if(item == null) return;
        if(item.getOwner() == null || player != item.getOwner()) return;

        Properties properties = new Properties();
        properties.set(AbilityProperties.CASTER, player);
        properties.set(AbilityProperties.ITEM_STACK, stack);
        properties.set(AbilityProperties.EVENT, event);
        properties.set(AbilityProperties.TARGETS, new ArrayList<>(Arrays.asList(event.getEntity())));
        properties.set(AbilityProperties.DROPS, event.getDrops());

        item.castAbility(ItemAction.ON_KILL_ENTITY, properties);
    }

    /**
     *  Event that triggers casting an items ability when a block is broken
     */
    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = player.getInventory().getItemInMainHand();

        String itemId = this.getItemIdFromStack(stack);

        if(itemId == null) return;

        Item item = this.createItem(player, itemId);

        if(item == null) return;
        if(item.getOwner() == null || player != item.getOwner()) return;

        Properties properties = new Properties();
        properties.set(AbilityProperties.CASTER, player);
        properties.set(AbilityProperties.ITEM_STACK, stack);
        properties.set(AbilityProperties.BLOCK, event.getBlock());
        properties.set(AbilityProperties.EVENT, event);

        item.castAbility(ItemAction.ON_BREAK_BLOCK, properties);
    }

    /**
     *  Event that triggers casting an items ability when the item is consumed (food, potions)
     */
    @EventHandler
    public void onPlayerEat(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = player.getInventory().getItemInMainHand();

        String itemId = this.getItemIdFromStack(stack);

        if(itemId == null) return;

        Item item = this.createItem(player, itemId);

        if(item == null) return;
        if(item.getOwner() == null || player != item.getOwner()) return;

        Properties properties = new Properties();
        properties.set(AbilityProperties.CASTER, player);
        properties.set(AbilityProperties.ITEM_STACK, stack);
        properties.set(AbilityProperties.EVENT, event);

        item.castAbility(ItemAction.ON_CONSUME_ITEM, properties);
    }

    /**
     *  Event that triggers casting an items ability when a fish is caught
     */
    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if(event.getState() != PlayerFishEvent.State.CAUGHT_ENTITY && event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;

        Player player = event.getPlayer();
        ItemStack stack = player.getInventory().getItemInMainHand();
        String itemId = this.getItemIdFromStack(stack);

        if(itemId == null) return;

        Item item = this.createItem(player, itemId);

        if(item == null) return;
        if(item.getOwner() == null || player != item.getOwner()) return;

        Properties properties = new Properties();
        properties.set(AbilityProperties.CASTER, event.getPlayer());
        properties.set(AbilityProperties.EVENT, event);
        properties.set(AbilityProperties.CAUGHT, event.getCaught());
        properties.set(AbilityProperties.HOOK, event.getHook());

        item.castAbility(ItemAction.ON_CATCH_FISH, properties);
    }

    /**
     *  Event that triggers casting an items ability on swapping offhand (pushing F)
     */
    @EventHandler
    public void onPlayerOffhand(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = event.getOffHandItem();
        String itemId = this.getItemIdFromStack(stack);

        if(itemId == null) return;

        Item item = this.createItem(player, itemId);

        if(item == null) return;
        if(item.getOwner() == null || player != item.getOwner()) return;

        if(item.getAbility(ItemAction.ON_SWAP_OFFHAND) != null) {
            Properties properties = new Properties();
            properties.set(AbilityProperties.CASTER, player);
            properties.set(AbilityProperties.ITEM_STACK, stack);
            properties.set(AbilityProperties.EVENT, event);

            event.setCancelled(true);

            item.castAbility(ItemAction.ON_SWAP_OFFHAND, properties);
        }
    }

    /**
     *  Event that triggers casting an items ability on shooting a bow
     */
    @EventHandler
    public void onLivingEntityShootBow(EntityShootBowEvent event) {
        LivingEntity entity = event.getEntity();
        ItemStack stack = event.getBow();
        String itemId = this.getItemIdFromStack(stack);

        if(itemId == null) return;

        Item item = this.createItem(entity, itemId);

        if(item == null) return;
        if(item.getOwner() == null || entity != item.getOwner()) return;
        if(item.getAbility(ItemAction.ON_SHOOT) == null) return;

        Properties properties = new Properties();
        properties.set(AbilityProperties.CASTER, entity);
        properties.set(AbilityProperties.ITEM_STACK, stack);
        properties.set(AbilityProperties.FORCE, event.getForce());
        properties.set(AbilityProperties.VELOCITY, event.getProjectile().getVelocity());
        properties.set(AbilityProperties.EVENT, event);

        event.setCancelled(true);

        item.castAbility(ItemAction.ON_SHOOT, properties);
    }

    /**
     *  Event that triggers casting an items ability on throwing an egg
     */
    @EventHandler
    public void onPlayerThrowEgg(PlayerEggThrowEvent event) {
        LivingEntity entity = event.getPlayer();
        ItemStack stack = event.getEgg().getItem();
        String itemId = this.getItemIdFromStack(stack);

        if(itemId == null) return;

        Item item = this.createItem(entity, itemId);

        if(item == null) return;
        if(item.getOwner() == null || entity != item.getOwner()) return;
        if(item.getAbility(ItemAction.ON_SHOOT) == null) return;

        Properties properties = new Properties();
        properties.set(AbilityProperties.CASTER, entity);
        properties.set(AbilityProperties.ITEM_STACK, stack);
        properties.set(AbilityProperties.FORCE, 1.0f);
        properties.set(AbilityProperties.VELOCITY, event.getEgg().getVelocity());
        properties.set(AbilityProperties.EVENT, event);

        event.setHatching(false);

        item.castAbility(ItemAction.ON_SHOOT, properties);
    }

    /**
     *  Event that triggers casting an items ability on sneak
     */
    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        if(!event.isSneaking()) return;

        Player player = event.getPlayer();
        ItemStack stack = player.getInventory().getItemInMainHand();
        String itemId = this.getItemIdFromStack(stack);

        if(itemId == null) return;

        Item item = this.createItem(player, itemId);

        if(item == null) return;
        if(item.getOwner() == null || player != item.getOwner()) return;

        Properties properties = new Properties();
        properties.set(AbilityProperties.CASTER, player);
        properties.set(AbilityProperties.ITEM_STACK, stack);
        properties.set(AbilityProperties.EVENT, event);

        item.castAbility(ItemAction.ON_SNEAK, properties);
    }
}
