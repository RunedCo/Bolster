package co.runed.bolster.managers;

import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.abilities.AbilityProvider;
import co.runed.bolster.abilities.AbilityProviderType;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.entity.BolsterEntity;
import co.runed.bolster.events.entity.EntityCastAbilityEvent;
import co.runed.bolster.items.Item;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.registries.Registries;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemManager extends Manager
{
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
        return this.getItem(entity, Registries.ITEMS.getId(itemClass));
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
        List<Item> items = this.getItems(entity);

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
        return this.createItem(entity, Registries.ITEMS.getId(itemClass));
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
        // NOTE: must not create the item instance unless there is no item instance of the same type created already
        //       if we create the item instance it causes high cpu load due to constantly remaking any ticking ability instances
        Item existingItem = (Item) AbilityManager.getInstance().getProvider(entity, AbilityProviderType.ITEM, id);
        Item newItem = existingItem != null ? existingItem : Registries.ITEMS.get(id);
        Item item = (Item) AbilityManager.getInstance().addProvider(entity, newItem);

        if (item == null) return null;

        boolean existing = item.equals(newItem);

        item.setEntity(entity);
        item.rebuild();

        if (!existing) AbilityManager.getInstance().trigger(entity, item, AbilityTrigger.CREATE_ITEM, new Properties());

        return item;
    }

    /**
     * @see #setItem(LivingEntity, Inventory, int, String, int)
     */
    public Item setItem(LivingEntity entity, Inventory inventory, int slot, Class<? extends Item> itemClass, int amount)
    {
        return this.setItem(entity, inventory, slot, Registries.ITEMS.getId(itemClass), amount);
    }

    /**
     * Creates an item instance for the player and gives them an amount in their inventory
     *
     * @param entity    the entity
     * @param inventory the inventory
     * @param slot      the slot
     * @param itemId    the item id
     * @param amount    the amount of items to give
     * @return the item instance
     */
    public Item setItem(LivingEntity entity, Inventory inventory, int slot, String itemId, int amount)
    {
        Item item = this.createItem(entity, itemId);

        if (item == null) return null;

        ItemStack stack = item.toItemStack();
        stack.setAmount(amount);

        Properties properties = new Properties();
        properties.set(AbilityProperties.ITEM_STACK, stack);
        properties.set(AbilityProperties.ITEM, item);
        properties.set(AbilityProperties.INVENTORY, inventory);
        properties.set(AbilityProperties.SLOT, amount);
        AbilityManager.getInstance().trigger(entity, item, AbilityTrigger.GIVE_ITEM, properties);

        inventory.setItem(slot, stack);

        return item;
    }

    /**
     * @see #giveItem(LivingEntity, Inventory, String, int)
     */
    public Item giveItem(LivingEntity entity, Inventory inventory, Class<? extends Item> itemClass, int amount)
    {
        return this.giveItem(entity, inventory, Registries.ITEMS.getId(itemClass), amount);
    }

    /**
     * Creates an item instance for the player and gives them an amount in their inventory
     *
     * @param entity    the entity
     * @param inventory the inventory
     * @param itemId    the item id
     * @param amount    the amount of items to give
     * @return the item instance
     */
    public Item giveItem(LivingEntity entity, Inventory inventory, String itemId, int amount)
    {
        Item item = this.createItem(entity, itemId);

        if (item == null) return null;

        ItemStack stack = item.toItemStack();
        stack.setAmount(amount);

        Properties properties = new Properties();
        properties.set(AbilityProperties.ITEM_STACK, stack);
        properties.set(AbilityProperties.ITEM, item);
        properties.set(AbilityProperties.INVENTORY, inventory);
        AbilityManager.getInstance().trigger(entity, item, AbilityTrigger.GIVE_ITEM, properties);

        int amountRemaining = amount;
        int maxSize = stack.getMaxStackSize();

        while (amountRemaining > 0)
        {
            ItemStack itemStack = stack.clone();

            int removedAmount = Math.min(amountRemaining, maxSize);

            itemStack.setAmount(removedAmount);

            inventory.addItem(itemStack);

            amountRemaining -= removedAmount;
        }

        return item;
    }

    /**
     * Removes a number of an item from a player's inventory
     *
     * @param inventory the inventory
     * @param item      the item
     * @param count     the number of items to remove
     * @return true if there were enough items to successfully remove
     */
    public boolean removeItem(Inventory inventory, Item item, int count)
    {
        ItemStack stack = item.toItemStack();
        stack.setAmount(count);

        if (!inventory.containsAtLeast(stack, count))
        {
            LivingEntity entity = inventory.getViewers().size() > 1 ? inventory.getViewers().get(0) : null;

            // TODO this is a bit jank
            if (entity != null && item.shouldClearOnRemove() && !this.anyInventoryContainsAtLeast(entity, item.getId(), 1))
            {
                this.clearItem(entity, item);
            }

            inventory.removeItem(stack);

            return false;
        }

//        ItemStack mainHand = inv.getItemInMainHand();
//        String itemId = this.getItemIdFromStack(mainHand);
//        int remaining = count;
//
//        if (itemId != null && itemId.equals(item.getId()))
//        {
//            remaining = count - mainHand.getAmount();
//
//            if (remaining >= 0)
//            {
//                inv.setItemInMainHand(new ItemStack(Material.AIR));
//
//                stack.setAmount(remaining);
//            }
//            else
//            {
//                mainHand.setAmount(mainHand.getAmount() - count);
//            }
//        }

        inventory.removeItem(stack);

        return true;
    }

    public boolean inventoryContainsAtLeast(Inventory inventory, Class<? extends Item> item, int count)
    {
        return this.inventoryContainsAtLeast(inventory, Registries.ITEMS.getId(item), count);
    }

    public boolean inventoryContainsAtLeast(Inventory inventory, String itemId, int count)
    {
        int numberFound = 0;

        for (ItemStack stack : inventory)
        {
            String stackItemId = this.getItemIdFromStack(stack);

            if (stackItemId == null) continue;

            if (stackItemId.equals(itemId)) numberFound += stack.getAmount();

            if (numberFound >= count) return true;
        }

        return false;
    }

    public int getItemCount(Inventory inventory, String itemId)
    {
        int numberFound = 0;

        for (ItemStack stack : inventory)
        {
            String stackItemId = this.getItemIdFromStack(stack);

            if (stackItemId == null) continue;

            if (stackItemId.equals(itemId)) numberFound += stack.getAmount();
        }

        return numberFound;
    }

    public int getAllInventoryItemCount(LivingEntity entity, Class<? extends Item> itemId)
    {
        return this.getAllInventoryItemCount(entity, Registries.ITEMS.getId(itemId));
    }

    public int getAllInventoryItemCount(LivingEntity entity, String itemId)
    {
        int numberFound = 0;

        for (Inventory inventory : BolsterEntity.from(entity).getInventories())
        {
            numberFound += getItemCount(inventory, itemId);
        }

        return numberFound;
    }

    public boolean anyInventoryContainsAtLeast(LivingEntity entity, String itemId, int count)
    {
        for (Inventory inventory : BolsterEntity.from(entity).getInventories())
        {
            boolean success = this.inventoryContainsAtLeast(inventory, itemId, count);

            if (success) return true;
        }

        return false;
    }

    public void rebuildAllItemStacks(LivingEntity entity)
    {
        for (Item item : this.getItems(entity))
        {
            this.rebuildItemStack(entity, item.getId());
        }
    }

    public void rebuildItemStack(LivingEntity entity, Class<? extends Item> item)
    {
        this.rebuildItemStack(entity, Registries.ITEMS.getId(item));
    }

    public void rebuildItemStack(LivingEntity entity, String itemId)
    {
        Item item = this.getItem(entity, itemId);

        if (item == null) return;

        for (Inventory inventory : BolsterEntity.from(entity).getInventories())
        {
            for (int i = 0; i < inventory.getSize(); i++)
            {
                ItemStack stack = inventory.getItem(i);

                String stackItemId = this.getItemIdFromStack(stack);

                if (stackItemId == null || !stackItemId.equals(itemId)) continue;

                ItemStack newStack = item.toItemStack().clone();
                newStack.setAmount(stack.getAmount());

                if (!stack.equals(newStack)) inventory.setItem(i, newStack);
            }
        }
    }

    public boolean areStacksSimilar(ItemStack stack1, ItemStack stack2)
    {
        String itemId1 = this.getItemIdFromStack(stack1);
        String itemId2 = this.getItemIdFromStack(stack2);

        return itemId1 != null && itemId2 != null ? itemId1.equals(itemId2) : stack1.isSimilar(stack2);
    }

    public boolean areStacksEqual(ItemStack stack1, ItemStack stack2)
    {
        String itemId1 = this.getItemIdFromStack(stack1);
        String itemId2 = this.getItemIdFromStack(stack2);

        return itemId1 != null && itemId2 != null ? stack1.getAmount() != stack2.getAmount() && itemId1.equals(itemId2) : stack1.equals(stack2);
    }

    /**
     * Clear a specific item instance from an entity
     *
     * @param entity the entity
     * @param item   the item
     */
    public void clearItem(LivingEntity entity, Item item)
    {
        if (!AbilityManager.getInstance().hasExactProvider(entity, item)) return;

        item.destroy();

        AbilityManager.getInstance().removeProvider(entity, item);
    }

    /**
     * Remove all item instances from an entity
     *
     * @param entity the entity
     */
    public void clearItems(LivingEntity entity)
    {
        AbilityManager.getInstance().reset(entity, AbilityProviderType.ITEM);

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
        return AbilityManager.getInstance().hasProvider(entity, AbilityProviderType.ITEM, id);
    }

    /**
     * Get a list of every item a player has an instance created for
     *
     * @param entity the entity
     * @return a list of items
     */
    public List<Item> getItems(LivingEntity entity)
    {
        return AbilityManager.getInstance().getProviders(entity, AbilityProviderType.ITEM).stream().map((prov) -> (Item) prov).collect(Collectors.toList());
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
        if (stack.getType() == Material.AIR) return null;
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
     * @param slot   the slot
     * @return
     */
    public boolean isItemEquipped(LivingEntity entity, Item item, EquipmentSlot slot)
    {
        EntityEquipment inv = entity.getEquipment();
        ItemStack stack = inv.getItem(slot);
        String itemId = this.getItemIdFromStack(stack);

        if (item.getEntity() == null || entity != item.getEntity()) return false;
        if (itemId == null) return false;
        if (item.getId() == null) return false;

        return itemId.equals(item.getId());
    }

    /**
     * Check if an entity has a specific item equipped
     *
     * @param entity the entity
     * @param item   the item class
     * @param slot   the slot
     * @return
     */
    public boolean isItemEquipped(LivingEntity entity, Class<? extends Item> item, EquipmentSlot slot)
    {
        Item itemInHand = this.getEquippedItem(entity, slot);
        return itemInHand != null && itemInHand.getId().equals(Registries.ITEMS.getId(item));
    }

    public Item getEquippedItem(LivingEntity entity, EquipmentSlot slot)
    {
        EntityEquipment inv = entity.getEquipment();
        ItemStack stack = inv.getItem(slot);
        String itemId = this.getItemIdFromStack(stack);

        if (itemId == null) return null;

        return this.createItem(entity, itemId);
    }

    /**
     * Reset the {@link ItemManager}
     */
    public void reset()
    {
        // TODO
        //this.entityItems.clear();
    }

//    @EventHandler
//    private void onPreCastAbility(EntityPreCastAbilityEvent event)
//    {
////        LivingEntity entity = event.getEntity();
////        EntityEquipment inv = entity.getEquipment();
////        ItemStack stack = inv.getItemInMainHand();
////
////        String itemId = this.getItemIdFromStack(stack);
////
////        if (itemId == null) return;
////
////        Item item = this.createItem(entity, itemId);
//
//        LivingEntity entity = event.getEntity();
//
//        for (Inventory inventory : BolsterEntity.from(entity).getAllInventories())
//        {
//            for (ItemStack item : inventory)
//            {
//                String itemId = this.getItemIdFromStack(item);
//
//                if (itemId == null) continue;
//
//                if (!this.hasItem(entity, itemId)) this.createItem(entity, itemId);
//            }
//        }
//
//        this.rebuildAllItemStacks(entity);
//    }

    @EventHandler
    private void onCastAbility(EntityCastAbilityEvent event)
    {
        AbilityProvider abilityProvider = event.getAbility().getAbilityProvider();
        Properties properties = event.getProperties();

        Item item = null;

        if (abilityProvider instanceof Item)
        {
            item = (Item) abilityProvider;
        }
        else if (properties.get(AbilityProperties.ITEM_STACK) != null)
        {
            String itemId = this.getItemIdFromStack(properties.get(AbilityProperties.ITEM_STACK));

            if (itemId != null)
            {
                item = this.createItem(event.getEntity(), itemId);
            }
        }

        properties.set(AbilityProperties.ITEM, item);
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();

        for (Inventory inventory : BolsterEntity.from(player).getInventories())
        {
            for (ItemStack item : inventory)
            {
                String itemId = this.getItemIdFromStack(item);

                if (itemId == null) continue;

                this.createItem(player, itemId);
            }
        }

        this.rebuildAllItemStacks(player);
    }

    // TODO: WOULD BE GOOD FOR OPTIMIZATION TO REMOVE ALL ITEM INSTANCES BUT MAY CAUSE ISSUES WITH PLAYERS DCING MID GAME
    // TODO: MAYBE ADD TO 5 MIN TIMER/DELAY?
    /*@EventHandler
    private void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        this.clearItems(player);
    }*/

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onFatalDamage(EntityDamageEvent event)
    {
        if (!(event.getEntity() instanceof LivingEntity)) return;

        LivingEntity entity = (LivingEntity) event.getEntity();

        if (event.getFinalDamage() >= entity.getHealth())
        {
            AbilityManager.getInstance().reset(entity, AbilityProviderType.ITEM);
        }
    }

//    @EventHandler
//    private void onPlayerDie(PlayerDeathEvent event)
//    {
//        Player player = event.getEntity();
//
//        this.clearItems(player);
//
//        AbilityManager.getInstance().reset(event.getEntity(), AbilityProviderType.ITEM);
//    }

    @EventHandler
    private void onDropItem(PlayerDropItemEvent event)
    {
        Player player = event.getPlayer();
        ItemStack stack = event.getItemDrop().getItemStack();
        String itemId = this.getItemIdFromStack(stack);

        if (itemId == null) return;

        Item item = this.createItem(player, itemId);

        if (item == null) return;
        if (item.getEntity() == null || player != item.getEntity()) return;
        if (!item.isDroppable())
        {
            event.setCancelled(true);
            return;
        }

        //this.removeItem(player.getInventory(), item);
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
