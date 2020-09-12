package co.runed.bolster.managers;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.abilities.AbilityProvider;
import co.runed.bolster.events.EntityCastAbilityEvent;
import co.runed.bolster.events.EntityPreCastAbilityEvent;
import co.runed.bolster.items.Item;
import co.runed.bolster.util.Manager;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ItemManager extends Manager
{
    Plugin plugin;

    private final HashMap<UUID, List<Item>> entityItems = new HashMap<>();

    private static ItemManager _instance;

    public ItemManager(Plugin plugin)
    {
        super(plugin);
        _instance = this;
    }

    /**
     * @param itemClass the class of the item as registered in {@link co.runed.bolster.util.registries.Registry<Item>}
     * @see #getItem(LivingEntity, String)
     */
    public Item getItem(LivingEntity entity, Class<? extends Item> itemClass)
    {
        return this.getItem(entity, Bolster.getItemRegistry().getId(itemClass));
    }

    /**
     * Gets an {@link Item} from a {@link LivingEntity}
     *
     * @param entity the entity that has the item
     * @param id     the {@link String} id of the item as registered in {@link co.runed.bolster.util.registries.Registry<Item>}
     * @return the existing {@link Item} instance or null
     */
    public Item getItem(LivingEntity entity, String id)
    {
        this.entityItems.putIfAbsent(entity.getUniqueId(), new ArrayList<>());

        List<Item> items = this.entityItems.get(entity.getUniqueId());

        for (Item item : items)
        {
            if (item.getId().equals(id)) return item;
        }

        return null;
    }

    /**
     * @param itemClass the class of the item as registered in {@link co.runed.bolster.util.registries.Registry<Item>}
     * @see #createItem(LivingEntity, String)
     */
    public Item createItem(LivingEntity entity, Class<? extends Item> itemClass)
    {
        return this.createItem(entity, Bolster.getItemRegistry().getId(itemClass));
    }

    /**
     * Creates an {@link Item} instance unless entity already has one
     * in which case it gets the existing instance
     *
     * @param entity the entity
     * @param id     the {@link String} id of the item as registered in {@link co.runed.bolster.util.registries.Registry<Item>}
     * @return the item instance
     */
    public Item createItem(LivingEntity entity, String id)
    {
        this.entityItems.putIfAbsent(entity.getUniqueId(), new ArrayList<>());

        List<Item> items = this.entityItems.get(entity.getUniqueId());

        // CHECK IF ITEM INSTANCE ALREADY EXISTS FOR PLAYER

        Item item = this.getItem(entity, id);

        if (item != null)
        {
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

    /**
     * {@code amount} defaults to {@code 1}
     *
     * @see #giveItem(Player, String, int)
     */
    public Item giveItem(Player player, Class<? extends Item> itemClass)
    {
        return this.giveItem(player, itemClass, 1);
    }

    /**
     * @see #giveItem(Player, String, int)
     */
    public Item giveItem(Player player, Class<? extends Item> itemClass, int amount)
    {
        return this.giveItem(player, Bolster.getItemRegistry().getId(itemClass), amount);
    }

    /**
     * {@code amount} defaults to {@code 1}
     *
     * @see #giveItem(Player, String, int)
     */
    public Item giveItem(Player player, String itemId)
    {
        return this.giveItem(player, itemId, 1);
    }

    /**
     * Creates an item instance for the player and gives them an amount in their inventory
     *
     * @param player the player
     * @param itemId the item id
     * @param amount the amount of items to give
     * @return the item instance
     */
    public Item giveItem(Player player, String itemId, int amount)
    {
        Item item = this.createItem(player, itemId);

        ItemStack stack = item.toItemStack();
        stack.setAmount(amount);

        PlayerInventory inv = player.getInventory();

        if (inv.getItemInMainHand().getType() == Material.AIR)
        {
            inv.setItemInMainHand(stack);
            return item;
        }

        inv.addItem(stack);

        return item;
    }

    /**
     * {@code amount} defaults to {@code 1}
     *
     * @see #removeItem(Player, Item, int)
     */
    public boolean removeItem(Player player, Item item)
    {
        return this.removeItem(player, item, 1);
    }

    /**
     * Removes a number of an item from a player's inventory
     *
     * @param player the player
     * @param item   the item
     * @param count  the number of items to remove
     * @return true if there were enough items to successfully remove
     */
    public boolean removeItem(Player player, Item item, int count)
    {
        PlayerInventory inv = player.getInventory();
        ItemStack stack = item.toItemStack();
        stack.setAmount(count);

        if (!inv.containsAtLeast(stack, count)) return false;

        ItemStack mainHand = inv.getItemInMainHand();
        String itemId = this.getItemIdFromStack(mainHand);
        int remaining = 0;

        if (itemId != null && itemId.equals(item.getId()))
        {
            remaining = count - mainHand.getAmount();

            if (remaining >= 0)
            {
                inv.setItemInMainHand(new ItemStack(Material.AIR));

                stack.setAmount(remaining);
            }
            else
            {
                mainHand.setAmount(mainHand.getAmount() - count);
            }
        }

        if (remaining > 0) inv.removeItem(stack);

        if (!inv.containsAtLeast(stack, 1)) this.clearItem(player, item);

        return true;
    }

    /**
     * Clear a specific item instance from an entity
     *
     * @param entity the entity
     * @param item   the item
     */
    public void clearItem(LivingEntity entity, Item item)
    {
        if (!this.entityItems.containsKey(entity.getUniqueId())) return;

        item.destroy();

        this.entityItems.get(entity.getUniqueId()).remove(item);
    }

    /**
     * Remove all item instances from an entity
     *
     * @param entity the entity
     */
    public void clearItems(LivingEntity entity)
    {
        List<Item> items = new ArrayList<>(this.getItems(entity));

        for (Item item : items)
        {
            this.clearItem(entity, item);
        }
    }

    /**
     * Check whether a player has an item instance
     *
     * @param entity the entity
     * @param id     the item id
     * @return
     */
    public boolean hasItem(LivingEntity entity, String id)
    {
        return this.getItem(entity, id) != null;
    }

    /**
     * Get a list of every item a player has an instance created for
     *
     * @param entity the entity
     * @return a list of items
     */
    public List<Item> getItems(LivingEntity entity)
    {
        if (!this.entityItems.containsKey(entity.getUniqueId())) return new ArrayList<>();

        return this.entityItems.get(entity.getUniqueId());
    }

    /**
     * Gets the item id from an item stack
     *
     * @param stack the item stack
     * @return the item id
     */
    public String getItemIdFromStack(ItemStack stack)
    {
        if (stack == null) return null;
        if (!stack.hasItemMeta()) return null;

        ItemMeta meta = stack.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        return pdc.get(Item.ITEM_ID_KEY, PersistentDataType.STRING);
    }

    /**
     * Check if an entity has a specific item equipped
     *
     * @param entity the entity
     * @param item   the item
     * @param slot the slot
     * @return
     */
    public boolean isItemEquipped(LivingEntity entity, Item item, EquipmentSlot slot)
    {
        EntityEquipment inv = entity.getEquipment();
        ItemStack stack = inv.getItem(slot);
        String itemId = this.getItemIdFromStack(stack);

        if (item.getOwner() == null || entity != item.getOwner()) return false;
        if (itemId == null) return false;
        if (item.getId() == null) return false;

        return itemId.equals(item.getId());
    }

    /**
     * Check if an entity has a specific item equipped
     *
     * @param entity the entity
     * @param item   the item class
     * @param slot the slot
     * @return
     */
    public boolean isItemEquipped(LivingEntity entity, Class<? extends Item> item, EquipmentSlot slot)
    {
        EntityEquipment inv = entity.getEquipment();
        ItemStack stack = inv.getItem(slot);
        String itemId = this.getItemIdFromStack(stack);

        if (itemId == null) return false;

        return itemId.equals(Bolster.getItemRegistry().getId(item));
    }

    /**
     * Reset the {@link ItemManager}
     */
    public void reset()
    {
        this.entityItems.clear();
    }

    @EventHandler
    private void onPreCastAbility(EntityPreCastAbilityEvent event)
    {
        LivingEntity entity = event.getEntity();
        EntityEquipment inv = entity.getEquipment();
        ItemStack stack = inv.getItemInMainHand();

        String itemId = this.getItemIdFromStack(stack);

        if (itemId == null) return;

        Item item = this.createItem(entity, itemId);
    }

    @EventHandler
    private void onCastAbility(EntityCastAbilityEvent event)
    {
        AbilityProvider abilitySource = event.getAbility().getAbilityProvider();

        if (abilitySource instanceof Item)
        {
            event.getProperties().set(AbilityProperties.ITEM, (Item) abilitySource);
        }
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();

        for (ItemStack stack : player.getInventory())
        {
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
    private void onPlayerDie(PlayerDeathEvent event)
    {
        Player player = event.getEntity();

        this.clearItems(player);

        this.entityItems.remove(player.getUniqueId());
    }

    @EventHandler
    private void onDropItem(PlayerDropItemEvent event)
    {
        Player player = event.getPlayer();
        ItemStack stack = event.getItemDrop().getItemStack();
        String itemId = this.getItemIdFromStack(stack);

        if (itemId == null) return;

        Item item = this.createItem(player, itemId);

        if (item == null) return;
        if (item.getOwner() == null || player != item.getOwner()) return;

        this.removeItem(player, item);
    }

    @EventHandler
    private void onPickupItem(EntityPickupItemEvent event)
    {
        LivingEntity entity = event.getEntity();
        ItemStack stack = event.getItem().getItemStack();

        String itemId = this.getItemIdFromStack(stack);

        if (itemId == null) return;

        this.createItem(entity, itemId);
    }

    public static ItemManager getInstance()
    {
        return _instance;
    }
}
