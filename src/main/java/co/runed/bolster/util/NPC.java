package co.runed.bolster.util;

import co.runed.bolster.managers.NPCManager;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class NPC {
    LivingEntity entity;
    String name;
    String subtitle;
    WrappedGameProfile gameProfile;
    Location location;

    public BiConsumer<Player, Vector> onAnyClick = (p, v) -> {
    };

    public BiConsumer<Player, Vector> onRightClick = (p, v) -> {
    };

    public BiConsumer<Player, Vector> onLeftClick = (p, v) -> {
    };

    private PlayerDisguise disguise;

    private boolean removed = false;

    public NPC(String name, WrappedGameProfile gameProfile) {
        this.name = name;
        this.gameProfile = gameProfile;
    }

    public NPC setOnRightClick(BiConsumer<Player, Vector> onRightClick) {
        this.onRightClick = onRightClick;

        return this;
    }

    public NPC setOnLeftClick(BiConsumer<Player, Vector> onLeftClick) {
        this.onLeftClick = onLeftClick;

        return this;
    }

    public NPC setOnAnyClick(BiConsumer<Player, Vector> onAnyClick) {
        this.onAnyClick = onAnyClick;

        return this;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public void setEntity(LivingEntity entity) {
        this.entity = entity;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public PlayerDisguise createDisguise() {
        var disguise = new PlayerDisguise(gameProfile);
        disguise.setName(name);
        disguise.setDisplayedInTab(false);

        List<String> name = new ArrayList<>();
        name.add(this.name);

        if (subtitle != null) name.add(this.subtitle);

        disguise.setMultiName(name.toArray(new String[0]));

        this.disguise = disguise;

        return disguise;
    }

    public void remove() {
        if (removed) return;

        removed = true;

        entity.remove();
        disguise.removeDisguise();

        NPCManager.getInstance().remove(this);
    }

    public NPC spawn(Location location) {
        return spawn(this, location);
    }

    public static NPC spawn(NPC npc, Location location) {
        var entity = (MushroomCow) location.getWorld().spawnEntity(location, EntityType.MUSHROOM_COW);
        entity.setAI(false);
        entity.setSilent(true);
        entity.setCanPickupItems(false);
        entity.setPersistent(false);

        npc.setEntity(entity);

        var disguise = npc.createDisguise();

        if (disguise != null) DisguiseAPI.disguiseEntity(entity, disguise);

        NPCManager.getInstance().add(npc);

        npc.location = location;

        return npc;
    }
}
