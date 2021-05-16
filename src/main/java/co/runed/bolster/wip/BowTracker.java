package co.runed.bolster.wip;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BowTracker implements Listener
{
    private final Set<UUID> drawing = new HashSet<>();

    private static BowTracker _instance;

    public BowTracker()
    {
        _instance = this;
    }

    @EventHandler
    public void onDraw(PlayerInteractEvent e)
    {
        Player player = e.getPlayer();

        //On interact
        if (e.getItem() != null && e.getItem().getType() == Material.BOW && player.getInventory().contains(Material.ARROW))
        {
            if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
            {
                drawing.add(player.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onShoot(EntityShootBowEvent e)
    {
        if (e.getEntity() instanceof Player)
        {
            UUID uuid = e.getEntity().getUniqueId();

            drawing.remove(uuid);
        }
    }

    @EventHandler
    public void onChangeSlot(PlayerItemHeldEvent e)
    {
        UUID uuid = e.getPlayer().getUniqueId();

        drawing.remove(uuid);
    }

    public boolean isDrawingBow(Player player)
    {
        return this.drawing.contains(player.getUniqueId());
    }

    public static BowTracker getInstance()
    {
        return _instance;
    }
}
