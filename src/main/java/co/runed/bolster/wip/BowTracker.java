package co.runed.bolster.wip;

import co.runed.bolster.Bolster;
import co.runed.bolster.events.entity.EntityCleanupEvent;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class BowTracker implements Listener {
    private final Set<UUID> drawing = new HashSet<>();
    private final Set<Consumer<LivingEntity>> onCancelShoot = new HashSet<>();

    private static BowTracker _instance;

    public BowTracker() {
        _instance = this;

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Bolster.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Client.BLOCK_DIG) {
            @Override
            public void onPacketReceiving(PacketEvent e) {
                var player = e.getPlayer();
                var handItem = player.getInventory().getItemInMainHand();

                if (handItem.getType() == Material.BOW) {
                    var status = e.getPacket().getPlayerDigTypes().read(0);

                    if (status == EnumWrappers.PlayerDigType.RELEASE_USE_ITEM) {
                        onCancelDrawing(player);
                    }
                }
            }
        });
    }

    private void onCancelDrawing(LivingEntity entity) {
        drawing.remove(entity.getUniqueId());

        for (var func : onCancelShoot) {
            func.accept(entity);
        }
    }

    public void addOnCancelShoot(Consumer<LivingEntity> onCancel) {
        onCancelShoot.add(onCancel);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDraw(PlayerInteractEvent e) {
        var player = e.getPlayer();

        //On interact
        if (e.getItem() != null && e.getItem().getType() == Material.BOW && player.getInventory().contains(Material.ARROW)) {
            if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                drawing.add(player.getUniqueId());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onShoot(EntityShootBowEvent e) {
        if (e.getEntity() instanceof Player) {
            onCancelDrawing(e.getEntity());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChangeSlot(PlayerItemHeldEvent e) {
        var uuid = e.getPlayer().getUniqueId();

        drawing.remove(uuid);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onCleanupEntity(EntityCleanupEvent event) {
        if (event.isForced()) {
            this.drawing.remove(event.getUniqueId());
        }
    }

    public boolean isDrawingBow(Player player) {
        return this.drawing.contains(player.getUniqueId());
    }

    public static BowTracker getInstance() {
        return _instance;
    }
}
