package co.runed.bolster.wip;

import co.runed.bolster.events.AbilityPlaceBlockEvent;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

public class TestListener implements Listener
{
    @EventHandler
    public void resourcePackEvent(PlayerResourcePackStatusEvent event)
    {
        if (event.getStatus() == PlayerResourcePackStatusEvent.Status.DECLINED || event.getStatus() == PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD)
        {
            event.getPlayer().kickPlayer("You need to enable resource packs.");
        }
    }

    @EventHandler
    private void onAbilityPlaceBlock(AbilityPlaceBlockEvent event)
    {
        if (!(event.getEntity() instanceof Player)) return;

        LocalPlayer player = WorldGuardPlugin.inst().wrapPlayer((Player) event.getEntity());
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();

        if (!query.testState(BukkitAdapter.adapt(event.getBlock().getLocation()), player, Flags.BUILD))
        {
            event.setCancelled(true);
        }
    }
}
