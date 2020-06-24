package co.runed.bolster.managers;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.properties.AbilityProperties;
import co.runed.bolster.items.Item;
import co.runed.bolster.items.ItemAction;
import co.runed.bolster.properties.Properties;
import org.bukkit.Bukkit;
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

    public Item getItem(LivingEntity entity, Class<? extends Item> itemClass) {
        return this.getItem(entity, Bolster.getItemRegistry().getId(itemClass));
    }

    /**
     * Gets an {@link Item} from a {@link LivingEntity}
     *
     * @param entity the entity that has the item
     * @param id     the {@link String} id of the item as registered in {@link co.runed.bolster.registries.ItemRegistry}
     * @return the existing {@link Item} instance or null
     */
    public Item getItem(LivingEntity entity, String id) {
        this.entityItems.putIfAbsent(entity.getUniqueId(), new ArrayList<>());

        List<Item> items = this.entityItems.get(entity.getUniqueId());

        for (Item item : items) {
            if (item.getId().equals(id)) return item;
        }

        return null;
    }

    public Item createItem(LivingEntity entity, Class<? extends Item> itemClass) {
        return this.createItem(entity, Bolster.getItemRegistry().getId(itemClass));
    }

    /**
     * Creates an {@link Item} instance unless entity already has one
     * in which case it gets the existing instance
     *
     * @param entity the entity that the item should be created for
     * @param id     the {@link String} id of the item as registered in {@link co.runed.bolster.registries.ItemRegistry}
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

    public Item giveItem(Player player, Class<? extends Item> itemClass) {
        return this.giveItem(player, itemClass, 1);
    }

    public Item giveItem(Player player, Class<? extends Item> itemClass, int amount) {
        return this.giveItem(player, Bolster.getItemRegistry().getId(itemClass), amount);
    }

    public Item giveItem(Player player, String itemId) {
        return this.giveItem(player, itemId, 1);
    }

    public Item giveItem(Player player, String itemId, int amount) {
        Item item = this.createItem(player, itemId);

        ItemStack stack = item.toItemStack();
        stack.setAmount(amount);

        player.getInventory().addItem(stack);

        return item;
    }

    public void removeItem(Player player, Item item) {
        this.removeItem(player, item, 1);
    }

    public void removeItem(Player player, Item item, int count) {
        for (ItemStack stack : player.getInventory()) {
            String itemId = this.getItemIdFromStack(stack);

            if (itemId != null && itemId.equals(item.getId())) {
                ItemStack toRemove = stack.clone();
                toRemove.setAmount(count);

                player.getInventory().removeItem(toRemove);

                break;
            }
        }

        for (ItemStack stack : player.getInventory()) {
            String itemId = this.getItemIdFromStack(stack);

            if (itemId != null && itemId.equals(item.getId())) return;
        }

        this.clearItem(player, item);
    }

    public void clearItem(LivingEntity entity, Item item) {
        if (!this.entityItems.containsKey(entity.getUniqueId())) return;

        item.destroy();

        this.entityItems.get(entity.getUniqueId()).remove(item);
    }

    public void clearItems(LivingEntity entity) {
        List<Item> items = new ArrayList<>(this.getItems(entity));

        for (Item item : items) {
            this.clearItem(entity, item);
        }
    }

    public boolean hasItem(LivingEntity entity, String id) {
        return this.getItem(entity, id) != null;
    }

    public List<Item> getItems(LivingEntity entity) {
        if (!this.entityItems.containsKey(entity.getUniqueId())) return new ArrayList<>();

        return this.entityItems.get(entity.getUniqueId());
    }

    public String getItemIdFromStack(ItemStack stack) {
        if (stack == null) return null;
        if (!stack.hasItemMeta()) return null;

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
        if (item.getId() == null) return false;

        return itemId.equals(item.getId());
    }

    public void reset() {
        this.entityItems.clear();
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        for (ItemStack stack : player.getInventory()) {
            String itemId = this.getItemIdFromStack(stack);

            if (itemId == null) return;

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
    private void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        this.clearItems(player);
    }*/

    @EventHandler
    private void onPlayerDie(PlayerDeathEvent event) {
        Player player = event.getEntity();

        this.clearItems(player);

        this.entityItems.remove(player.getUniqueId());
    }

    @EventHandler
    private void onDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = event.getItemDrop().getItemStack();
        String itemId = this.getItemIdFromStack(stack);

        if (itemId == null) return;

        Item item = this.createItem(player, itemId);

        if (item == null) return;
        if (item.getOwner() == null || player != item.getOwner()) return;

        if (item.hasAbility(ItemAction.ON_DROP_ITEM)) {
            Properties properties = new Properties();
            properties.set(AbilityProperties.CASTER, player);
            properties.set(AbilityProperties.ITEM, item);
            properties.set(AbilityProperties.ITEM_STACK, stack);
            properties.set(AbilityProperties.EVENT, event);

            item.castAbility(ItemAction.ON_DROP_ITEM, properties);

            event.setCancelled(true);
            return;
        }

        this.removeItem(player, item);
    }

    @EventHandler
    private void onPickupItem(EntityPickupItemEvent event) {
        LivingEntity entity = event.getEntity();
        ItemStack stack = event.getItem().getItemStack();

        String itemId = this.getItemIdFromStack(stack);

        if (itemId == null) return;

        this.createItem(entity, itemId);
    }

    /**
     * Event that triggers casting an items ability on left or right click
     */
    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = event.getItem();
        String itemId = this.getItemIdFromStack(stack);

        if (itemId == null) return;

        Item item = this.createItem(player, itemId);

        if (item == null) return;
        if (item.getOwner() == null || player != item.getOwner()) return;

        Properties properties = new Properties();
        properties.set(AbilityProperties.CASTER, player);
        properties.set(AbilityProperties.ITEM, item);
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
     * Event that triggers casting an items ability when an entity is damaged
     */
    @EventHandler
    private void onDamageEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof LivingEntity)) return;

        LivingEntity entity = (LivingEntity) event.getDamager();
        EntityEquipment inv = entity.getEquipment();
        ItemStack stack = inv.getItemInMainHand();

        String itemId = this.getItemIdFromStack(stack);

        if (itemId == null) return;

        Item item = this.createItem(entity, itemId);

        if (item == null) return;
        if (item.getOwner() == null || entity != item.getOwner()) return;

        Properties properties = new Properties();
        properties.set(AbilityProperties.CASTER, entity);
        properties.set(AbilityProperties.TARGETS, new ArrayList<>(Arrays.asList((LivingEntity) event.getEntity())));
        properties.set(AbilityProperties.ITEM_STACK, stack);
        properties.set(AbilityProperties.EVENT, event);
        properties.set(AbilityProperties.DAMAGE, event.getDamage());

        item.castAbility(ItemAction.ON_DAMAGE_ENTITY, properties);
    }

    /**
     * Event that triggers casting an items ability when an entity is killed
     */
    @EventHandler
    private void onKillEntity(EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();

        ItemStack stack = player.getInventory().getItemInMainHand();

        String itemId = this.getItemIdFromStack(stack);

        if (itemId == null) return;

        Item item = this.createItem(player, itemId);

        if (item == null) return;
        if (item.getOwner() == null || player != item.getOwner()) return;

        Properties properties = new Properties();
        properties.set(AbilityProperties.CASTER, player);
        properties.set(AbilityProperties.ITEM, item);
        properties.set(AbilityProperties.ITEM_STACK, stack);
        properties.set(AbilityProperties.EVENT, event);
        properties.set(AbilityProperties.TARGETS, new ArrayList<>(Arrays.asList(event.getEntity())));
        properties.set(AbilityProperties.DROPS, event.getDrops());

        item.castAbility(ItemAction.ON_KILL_ENTITY, properties);
    }

    /**
     * Event that triggers casting an items ability when a block is broken
     */
    @EventHandler
    private void onPlayerBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = player.getInventory().getItemInMainHand();

        String itemId = this.getItemIdFromStack(stack);

        if (itemId == null) return;

        Item item = this.createItem(player, itemId);

        if (item == null) return;
        if (item.getOwner() == null || player != item.getOwner()) return;

        Properties properties = new Properties();
        properties.set(AbilityProperties.CASTER, player);
        properties.set(AbilityProperties.ITEM, item);
        properties.set(AbilityProperties.ITEM_STACK, stack);
        properties.set(AbilityProperties.BLOCK, event.getBlock());
        properties.set(AbilityProperties.EVENT, event);

        item.castAbility(ItemAction.ON_BREAK_BLOCK, properties);
    }

    /**
     * Event that triggers casting an items ability when the item is consumed (food, potions)
     */
    @EventHandler
    private void onPlayerEat(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = player.getInventory().getItemInMainHand();

        String itemId = this.getItemIdFromStack(stack);

        if (itemId == null) return;

        Item item = this.createItem(player, itemId);

        if (item == null) return;
        if (item.getOwner() == null || player != item.getOwner()) return;

        Properties properties = new Properties();
        properties.set(AbilityProperties.CASTER, player);
        properties.set(AbilityProperties.ITEM, item);
        properties.set(AbilityProperties.ITEM_STACK, stack);
        properties.set(AbilityProperties.EVENT, event);

        item.castAbility(ItemAction.ON_CONSUME_ITEM, properties);
    }

    /**
     * Event that triggers casting an items ability when a fish is caught
     */
    @EventHandler
    private void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_ENTITY && event.getState() != PlayerFishEvent.State.CAUGHT_FISH)
            return;

        Player player = event.getPlayer();
        ItemStack stack = player.getInventory().getItemInMainHand();
        String itemId = this.getItemIdFromStack(stack);

        if (itemId == null) return;

        Item item = this.createItem(player, itemId);

        if (item == null) return;
        if (item.getOwner() == null || player != item.getOwner()) return;

        Properties properties = new Properties();
        properties.set(AbilityProperties.CASTER, event.getPlayer());
        properties.set(AbilityProperties.EVENT, event);
        properties.set(AbilityProperties.CAUGHT, event.getCaught());
        properties.set(AbilityProperties.HOOK, event.getHook());

        item.castAbility(ItemAction.ON_CATCH_FISH, properties);
    }

    /**
     * Event that triggers casting an items ability on swapping offhand (pushing F)
     */
    @EventHandler
    private void onPlayerOffhand(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = event.getOffHandItem();
        String itemId = this.getItemIdFromStack(stack);

        if (itemId == null) return;

        Item item = this.createItem(player, itemId);

        if (item == null) return;
        if (item.getOwner() == null || player != item.getOwner()) return;

        if (item.hasAbility(ItemAction.ON_SWAP_OFFHAND)) {
            Properties properties = new Properties();
            properties.set(AbilityProperties.CASTER, player);
            properties.set(AbilityProperties.ITEM, item);
            properties.set(AbilityProperties.ITEM_STACK, stack);
            properties.set(AbilityProperties.EVENT, event);

            event.setCancelled(true);

            item.castAbility(ItemAction.ON_SWAP_OFFHAND, properties);
        }
    }

    /**
     * Event that triggers casting an items ability on shooting a bow
     */
    @EventHandler
    private void onLivingEntityShootBow(EntityShootBowEvent event) {
        LivingEntity entity = event.getEntity();
        ItemStack stack = event.getBow();
        String itemId = this.getItemIdFromStack(stack);

        if (itemId == null) return;

        Item item = this.createItem(entity, itemId);

        if (item == null) return;
        if (item.getOwner() == null || entity != item.getOwner()) return;
        if (item.hasAbility(ItemAction.ON_SHOOT)) return;

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
     * Event that triggers casting an items ability on throwing an egg
     */
    @EventHandler
    private void onPlayerThrowEgg(PlayerEggThrowEvent event) {
        LivingEntity entity = event.getPlayer();
        ItemStack stack = event.getEgg().getItem();
        String itemId = this.getItemIdFromStack(stack);

        if (itemId == null) return;

        Item item = this.createItem(entity, itemId);

        if (item == null) return;
        if (item.getOwner() == null || entity != item.getOwner()) return;
        if (item.hasAbility(ItemAction.ON_SHOOT)) return;

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
     * Event that triggers casting an items ability on sneak
     */
    @EventHandler
    private void onPlayerSneak(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) return;

        Player player = event.getPlayer();
        ItemStack stack = player.getInventory().getItemInMainHand();
        String itemId = this.getItemIdFromStack(stack);

        if (itemId == null) return;

        Item item = this.createItem(player, itemId);

        if (item == null) return;
        if (item.getOwner() == null || player != item.getOwner()) return;

        Properties properties = new Properties();
        properties.set(AbilityProperties.CASTER, player);
        properties.set(AbilityProperties.ITEM, item);
        properties.set(AbilityProperties.ITEM_STACK, stack);
        properties.set(AbilityProperties.EVENT, event);

        item.castAbility(ItemAction.ON_SNEAK, properties);
    }
}
