package co.runed.bolster.wip;

import co.runed.bolster.events.world.CustomCanDestroyBlockEvent;
import co.runed.bolster.events.world.CustomCanPlaceBlockEvent;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class WorldGuardListener implements Listener {
    @EventHandler
    private void onAbilityPlaceBlock(CustomCanPlaceBlockEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        this.onBlockEvent(event, event.getBlock(), player, Flags.BUILD);
    }

    @EventHandler
    private void onAbilityDestroyBlock(CustomCanDestroyBlockEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        this.onBlockEvent(event, event.getBlock(), player, Flags.BUILD);
    }

    private void onBlockEvent(Cancellable event, Block block, Player player, StateFlag... flags) {
        var wePlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        var container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        var query = container.createQuery();

        if (!query.testState(BukkitAdapter.adapt(block.getLocation()), wePlayer, flags)) {
            event.setCancelled(true);
        }
    }
}
