package co.runed.bolster.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

/* Vaguely based on https://github.com/Arnuh/ArmorEquipEvent */
public class PlayerInventoryChangedEvent extends PlayerEvent implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    final int slot;
    final ItemStack previousStack;
    final ItemStack newStack;

    public PlayerInventoryChangedEvent(Player who, final int itemSlot, final ItemStack previousStack, final ItemStack newStack)
    {
        super(who);
        this.slot = itemSlot;
        this.previousStack = previousStack;
        this.newStack = newStack;
    }

    public int getSlot()
    {
        return slot;
    }

    public ItemStack getNewStack()
    {
        return newStack;
    }

    public ItemStack getPreviousStack()
    {
        return previousStack;
    }

    @Override
    public boolean isCancelled()
    {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean b)
    {
        this.cancelled = b;
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }

    public enum EquipMethod
    {
        /**
         * When you shift click an armor piece to equip or unequip
         */
        SHIFT_CLICK,
        /**
         * When you drag and drop the item to equip or unequip
         */
        DRAG,
        /**
         * When you manually equip or unequip the item. Use to be DRAG
         */
        PICK_DROP,
        /**
         * When you right click an armor piece in the hotbar without the inventory open to equip.
         */
        HOTBAR,
        /**
         * When you press the hotbar slot number while hovering over the armor slot to equip or unequip
         */
        HOTBAR_SWAP,
        /**
         * When in range of a dispenser that shoots an armor piece to equip.<br>
         * Requires the spigot version to have {@link org.bukkit.event.block.BlockDispenseArmorEvent} implemented.
         */
        DISPENSER,
        /**
         * When an armor piece is removed due to it losing all durability.
         */
        BROKE,
        /**
         * When you die causing all armor to unequip
         */
        DEATH,
        ;
    }
}
