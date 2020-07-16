package co.runed.bolster.managers;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.AbilityProvider;
import co.runed.bolster.abilities.events.EntityCastAbilityEvent;
import co.runed.bolster.abilities.events.EntityPreCastAbilityEvent;
import co.runed.bolster.abilities.properties.AbilityProperties;
import co.runed.bolster.items.Item;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ItemManager implements Listener
{
    Plugin plugin;

    private final HashMap<UUID, List<Item>> entityItems = new HashMap<>();

    public ItemManager(Plugin plugin)
    {
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public Item getItem(LivingEntity entity, Class<? extends Item> itemClass)
    {
        return this.getItem(entity, Bolster.getItemRegistry().getId(itemClass));
    }

    /**
     * Gets an {@link Item} from a {@link LivingEntity}
     *
     * @param entity the entity that has the item
     * @param id     the {@link String} id of the item as registered in {@link co.runed.bolster.registries.ItemRegistry}
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

    public Item createItem(LivingEntity entity, Class<? extends Item> itemClass)
    {
        return this.createItem(entity, Bolster.getItemRegistry().getId(itemClass));
    }

    /**
     * Creates an {@link Item} instance unless entity already has one
     * in which case it gets the existing instance
     *
     * @param entity the entity that the item should be created for
     * @param id     the {@link String} id of the item as registered in {@link co.runed.bolster.registries.ItemRegistry}
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

    public Item giveItem(Player player, Class<? extends Item> itemClass)
    {
        return this.giveItem(player, itemClass, 1);
    }

    public Item giveItem(Player player, Class<? extends Item> itemClass, int amount)
    {
        return this.giveItem(player, Bolster.getItemRegistry().getId(itemClass), amount);
    }

    public Item giveItem(Player player, String itemId)
    {
        return this.giveItem(player, itemId, 1);
    }

    public Item giveItem(Player player, String itemId, int amount)
    {
        Item item = this.createItem(player, itemId);

        ItemStack stack = item.toItemStack();
        stack.setAmount(amount);

        player.getInventory().addItem(stack);

        return item;
    }

    public void removeItem(Player player, Item item)
    {
        this.removeItem(player, item, 1);
    }

    public void removeItem(Player player, Item item, int count)
    {
        Inventory playerInv = player.getInventory();
        ItemStack stack = item.toItemStack();
        stack.setAmount(count);

        if (!playerInv.containsAtLeast(stack, count)) return;

        playerInv.removeItem(stack);

        if (!playerInv.containsAtLeast(stack, 1)) this.clearItem(player, item);
    }

    public void clearItem(LivingEntity entity, Item item)
    {
        if (!this.entityItems.containsKey(entity.getUniqueId())) return;

        item.destroy();

        this.entityItems.get(entity.getUniqueId()).remove(item);
    }

    public void clearItems(LivingEntity entity)
    {
        List<Item> items = new ArrayList<>(this.getItems(entity));

        for (Item item : items)
        {
            this.clearItem(entity, item);
        }
    }

    public boolean hasItem(LivingEntity entity, String id)
    {
        return this.getItem(entity, id) != null;
    }

    public List<Item> getItems(LivingEntity entity)
    {
        if (!this.entityItems.containsKey(entity.getUniqueId())) return new ArrayList<>();

        return this.entityItems.get(entity.getUniqueId());
    }

    public String getItemIdFromStack(ItemStack stack)
    {
        if (stack == null) return null;
        if (!stack.hasItemMeta()) return null;

        ItemMeta meta = stack.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        return pdc.get(Item.ITEM_ID_KEY, PersistentDataType.STRING);
    }

    public boolean isEntityHolding(LivingEntity entity, Item item)
    {
        EntityEquipment inv = entity.getEquipment();
        ItemStack stack = inv.getItemInMainHand();
        String itemId = this.getItemIdFromStack(stack);

        if (item.getOwner() == null || entity != item.getOwner()) return false;
        if (itemId == null) return false;
        if (item.getId() == null) return false;

        return itemId.equals(item.getId());
    }

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
        AbilityProvider abilitySource = event.getAbility().getAbilitySource();

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
}
