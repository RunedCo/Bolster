package co.runed.bolster.abilities.listeners;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.abilities.AbilityTrigger;
import co.runed.bolster.entity.BolsterLivingEntity;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Event that triggers casting an ability when an item is consumed (food, potions)
 */
public class PlayerEatListener implements Listener
{
    @EventHandler
    private void onPlayerEat(PlayerItemConsumeEvent event)
    {
        Player player = event.getPlayer();
        ItemStack stack = event.getItem();

        Properties properties = new Properties();
        properties.set(AbilityProperties.CASTER, BolsterLivingEntity.from(player));
        properties.set(AbilityProperties.WORLD, player.getWorld());
        properties.set(AbilityProperties.ITEM_STACK, stack);
        properties.set(AbilityProperties.EVENT, event);

        Bolster.getAbilityManager().trigger(player, AbilityTrigger.ON_CONSUME_ITEM, properties);
    }
}
