package co.runed.bolster.util.chat;

import co.runed.bolster.managers.Manager;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;

public class ChatManager extends Manager {
    private ChatRenderer chatRenderer = new BolsterChatRenderer();

    private static ChatManager _instance;

    public ChatManager(Plugin plugin) {
        super(plugin);

        _instance = this;
    }

    public void sendMessage(Player player, Component message) {
        Bukkit.broadcast(chatRenderer.render(player, player.displayName(), message, Audience.empty()));
    }

    @EventHandler
    private void onChatMessage(AsyncChatEvent event) {
        event.renderer(chatRenderer);
    }

    public static ChatManager getInstance() {
        return _instance;
    }
}
