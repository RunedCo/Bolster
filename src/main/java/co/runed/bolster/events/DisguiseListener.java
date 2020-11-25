package co.runed.bolster.events;

import co.runed.bolster.Bolster;
import co.runed.bolster.util.NetworkUtil;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.FlagWatcher;
import me.libraryaddict.disguise.disguisetypes.MetaIndex;
import me.libraryaddict.disguise.events.DisguiseEvent;
import me.libraryaddict.disguise.events.UndisguiseEvent;
import me.libraryaddict.disguise.utilities.json.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class DisguiseListener implements Listener
{
    private static Gson gson;

    public DisguiseListener()
    {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.disableHtmlEscaping();

        gsonBuilder.registerTypeAdapter(MetaIndex.class, new SerializerMetaIndex());
        gsonBuilder.registerTypeAdapter(WrappedGameProfile.class, new SerializerGameProfile());
        gsonBuilder.registerTypeAdapter(WrappedBlockData.class, new SerializerWrappedBlockData());
        gsonBuilder.registerTypeAdapter(WrappedChatComponent.class, new SerializerChatComponent());
        gsonBuilder.registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer());
        gsonBuilder.registerTypeHierarchyAdapter(ItemStack.class, new SerializerItemStack());

        gsonBuilder.registerTypeAdapter(FlagWatcher.class, new SerializerFlagWatcher(gsonBuilder.create()));
        gsonBuilder.registerTypeAdapter(Disguise.class, new SerializerDisguise());

        gson = gsonBuilder.create();
    }

    @EventHandler
    private void onDisguise(DisguiseEvent event)
    {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        EntityType entityType = event.getDisguise().getType().getEntityType();

        ByteBuf byteBuf = Unpooled.buffer();
        NetworkUtil.writeString(byteBuf, entityType.getKey().toString());
        NetworkUtil.writeString(byteBuf, gson.toJson(event.getDisguise()));

        player.sendPluginMessage(Bolster.getInstance(), "bolster:disguise", byteBuf.array());
    }

    @EventHandler
    private void onUndisguise(UndisguiseEvent event)
    {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();

        player.sendPluginMessage(Bolster.getInstance(), "bolster:undisguise", new byte[0]);
    }

    private static void serializeDisguise(Disguise disguise)
    {
    }
}
