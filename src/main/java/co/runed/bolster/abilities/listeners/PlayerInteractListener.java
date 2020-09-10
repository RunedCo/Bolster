package co.runed.bolster.abilities.listeners;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.managers.AbilityManager;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 * Event that triggers casting an ability on left or right click
 */
public class PlayerInteractListener implements Listener
{
    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event)
    {
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        ItemStack stack = event.getItem();

        Properties properties = new Properties();
        properties.set(AbilityProperties.ITEM_STACK, stack);
        properties.set(AbilityProperties.BLOCK_ACTION, event.getAction());
        properties.set(AbilityProperties.BLOCK, event.getClickedBlock());
        properties.set(AbilityProperties.BLOCK_FACE, event.getBlockFace());
        properties.set(AbilityProperties.EVENT, event);


        AbilityTrigger trigger = null;

        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
        {
            trigger = event.getAction() == Action.LEFT_CLICK_AIR ? AbilityTrigger.LEFT_CLICK_AIR : AbilityTrigger.LEFT_CLICK_BLOCK;

            AbilityManager.getInstance().trigger(player, AbilityTrigger.LEFT_CLICK, properties);
        }

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            trigger = event.getAction() == Action.RIGHT_CLICK_AIR ? AbilityTrigger.RIGHT_CLICK_AIR : AbilityTrigger.RIGHT_CLICK_BLOCK;

            AbilityManager.getInstance().trigger(player, AbilityTrigger.RIGHT_CLICK, properties);
        }

        AbilityManager.getInstance().trigger(player, trigger, properties);
    }
}
