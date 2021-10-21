package co.runed.bolster.events.entity;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class EntityCleanupEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    UUID uuid;
    LivingEntity entity;
    boolean forced;

    public EntityCleanupEvent(LivingEntity entity, boolean forced) {
        this(entity.getUniqueId(), forced);

        this.entity = entity;
    }

    public EntityCleanupEvent(UUID uuid, boolean forced) {
        super();

        this.uuid = uuid;
        this.forced = forced;
    }

    @Nullable
    public LivingEntity getEntity() {
        return entity;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public boolean isForced() {
        return forced;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
