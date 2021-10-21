package co.runed.bolster.events.player;

import co.runed.bolster.game.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SavePlayerDataEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    Player player;
    PlayerData playerData;

    public SavePlayerDataEvent(@Nullable Player player, @NotNull PlayerData playerData) {
        super();

        this.player = player;
        this.playerData = playerData;
    }

    @Nullable
    public Player getPlayer() {
        return player;
    }

    @NotNull
    public PlayerData getPlayerData() {
        return playerData;
    }

    public void setPlayerData(PlayerData playerData) {
        this.playerData = playerData;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
