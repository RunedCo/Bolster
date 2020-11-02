package co.runed.bolster.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class ArmorListener implements Listener
{
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public final void onInventoryClick(final InventoryClickEvent e)
    {
        boolean shift = false;
        boolean numberKey = false;

        if (e.getAction() == InventoryAction.NOTHING) return;// Why does this get called if nothing happens??

        shift = e.getClick().equals(ClickType.SHIFT_LEFT) || e.getClick().equals(ClickType.SHIFT_RIGHT);
        numberKey = e.getClick().equals(ClickType.NUMBER_KEY);

        if (e.getSlotType() != InventoryType.SlotType.ARMOR && e.getSlotType() != InventoryType.SlotType.QUICKBAR && e.getSlotType() != InventoryType.SlotType.CONTAINER)
            return;

        if (e.getClickedInventory() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) return;
        if (!e.getInventory().getType().equals(InventoryType.CRAFTING) && !e.getInventory().getType().equals(InventoryType.PLAYER))
            return;
        if (!(e.getWhoClicked() instanceof Player)) return;

        Player player = (Player) e.getWhoClicked();
        ItemStack cursor = e.getCursor();
        ItemStack currentItem = e.getCurrentItem();
        int slot = e.getSlot();
        int rawSlot = e.getRawSlot();

        player.sendMessage(cursor + " "
                + currentItem + " "
                + slot + " "
                + rawSlot + " "
                + cursor.equals(currentItem));

        //PlayerInventoryChangedEvent inventoryChangedEvent = new PlayerInventoryChangedEvent(player, slot, )

        //if (!shift && newArmorType != null && e.getRawSlot() != newArmorType.getSlot())
        //{
        // Used for drag and drop checking to make sure you aren't trying to place a helmet in the boots slot.
        //    return;
        //}



        /* if (shift)
        {
            newArmorType = ArmorType.matchType(e.getCurrentItem());
            if (newArmorType != null)
            {
                boolean equipping = true;
                if (e.getRawSlot() == newArmorType.getSlot())
                {
                    equipping = false;
                }
                if (newArmorType.equals(ArmorType.HELMET) && (equipping ? isAirOrNull(e.getWhoClicked().getInventory().getHelmet()) : !isAirOrNull(e.getWhoClicked().getInventory().getHelmet())) || newArmorType.equals(ArmorType.CHESTPLATE) && (equipping ? isAirOrNull(e.getWhoClicked().getInventory().getChestplate()) : !isAirOrNull(e.getWhoClicked().getInventory().getChestplate())) || newArmorType.equals(ArmorType.LEGGINGS) && (equipping ? isAirOrNull(e.getWhoClicked().getInventory().getLeggings()) : !isAirOrNull(e.getWhoClicked().getInventory().getLeggings())) || newArmorType.equals(ArmorType.BOOTS) && (equipping ? isAirOrNull(e.getWhoClicked().getInventory().getBoots()) : !isAirOrNull(e.getWhoClicked().getInventory().getBoots())))
                {
                    PlayerInventoryChangedEvent armorEquipEvent = new PlayerInventoryChangedEvent((Player) e.getWhoClicked(), PlayerInventoryChangedEvent.EquipMethod.SHIFT_CLICK, newArmorType, equipping ? null : e.getCurrentItem(), equipping ? e.getCurrentItem() : null);
                    Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
                    if (armorEquipEvent.isCancelled())
                    {
                        e.setCancelled(true);
                    }
                }
            }
        }
        else
        {
            ItemStack newArmorPiece = e.getCursor();
            ItemStack oldArmorPiece = e.getCurrentItem();
            if (numberKey)
            {
                if (e.getClickedInventory().getType().equals(InventoryType.PLAYER))
                {// Prevents shit in the 2by2 crafting
                    // e.getClickedInventory() == The players inventory
                    // e.getHotBarButton() == key people are pressing to equip or unequip the item to or from.
                    // e.getRawSlot() == The slot the item is going to.
                    // e.getSlot() == Armor slot, can't use e.getRawSlot() as that gives a hotbar slot ;-;
                    ItemStack hotbarItem = e.getClickedInventory().getItem(e.getHotbarButton());
                    if (!isAirOrNull(hotbarItem))
                    {// Equipping
                        newArmorType = ArmorType.matchType(hotbarItem);
                        newArmorPiece = hotbarItem;
                        oldArmorPiece = e.getClickedInventory().getItem(e.getSlot());
                    }
                    else
                    {// Unequipping
                        newArmorType = ArmorType.matchType(!isAirOrNull(e.getCurrentItem()) ? e.getCurrentItem() : e.getCursor());
                    }
                }
            }
            else
            {
                if (isAirOrNull(e.getCursor()) && !isAirOrNull(e.getCurrentItem()))
                {// unequip with no new item going into the slot.
                    newArmorType = ArmorType.matchType(e.getCurrentItem());
                }
                // e.getCurrentItem() == Unequip
                // e.getCursor() == Equip
                // newArmorType = ArmorType.matchType(!isAirOrNull(e.getCurrentItem()) ? e.getCurrentItem() : e.getCursor());
            }

            if (newArmorType != null && e.getRawSlot() == newArmorType.getSlot())
            {
                EquipMethod method = EquipMethod.PICK_DROP;
                if (e.getAction().equals(InventoryAction.HOTBAR_SWAP) || numberKey) method = EquipMethod.HOTBAR_SWAP;
                PlayerInventoryChangedEvent armorEquipEvent = new PlayerInventoryChangedEvent((Player) e.getWhoClicked(), method, newArmorType, oldArmorPiece, newArmorPiece);
                Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
                if (armorEquipEvent.isCancelled())
                {
                    e.setCancelled(true);
                }
            }
        }*/
    }
}
