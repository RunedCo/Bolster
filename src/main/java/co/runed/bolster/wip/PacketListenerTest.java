package co.runed.bolster.wip;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PacketListenerTest extends PacketAdapter
{
    public PacketListenerTest(Plugin plugin)
    {
        super(plugin, ListenerPriority.HIGH, PacketType.Play.Server.ENTITY_METADATA);
    }

    @Override
    public void onPacketSending(PacketEvent event)
    {
        if (event.isCancelled()) return;

        try
        {
            Player observer = event.getPlayer();

            PacketContainer packet = event.getPacket();
            if (packet.getIntegers().read(0) != observer.getEntityId())
            {
                return;
            }

            Disguise disguise = DisguiseAPI.getDisguise(observer, observer);
            if (disguise == null)
            {
                return;
            }


        }
        catch (Exception e)
        {

        }
    }
}
