package co.runed.bolster.abilities.listeners;

import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.managers.AbilityManager;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractAtEntityListener implements Listener
{
    @EventHandler
    public void onInteractAtEntity(PlayerInteractAtEntityEvent event)
    {
        Player player = event.getPlayer();
        EntityEquipment inv = player.getEquipment();
        ItemStack stack = inv.getItemInMainHand();

        Properties properties = new Properties();
        properties.set(AbilityProperties.EVENT, event);
        properties.set(AbilityProperties.ITEM_STACK, stack);
        properties.set(AbilityProperties.TARGET, event.getRightClicked());

        AbilityManager.getInstance().trigger(player, AbilityTrigger.RIGHT_CLICK_ENTITY, properties);
    }

    @EventHandler
    public void onInteractedWith(PlayerInteractAtEntityEvent event)
    {
        if (!(event.getRightClicked() instanceof LivingEntity)) return;

        LivingEntity entity = (LivingEntity) event.getRightClicked();
        EntityEquipment inv = entity.getEquipment();
        ItemStack stack = inv.getItemInMainHand();

        Properties properties = new Properties();
        properties.set(AbilityProperties.EVENT, event);
        properties.set(AbilityProperties.ITEM_STACK, stack);
        properties.set(AbilityProperties.TARGET, event.getPlayer());

        AbilityManager.getInstance().trigger(entity, AbilityTrigger.ON_INTERACTED_WITH, properties);
    }
}
