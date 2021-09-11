package co.runed.bolster.util.chat;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatListener implements Listener {
    private ChatRenderer chatRenderer = new BolsterChatRenderer();

    @EventHandler
    private void onChatMessage(AsyncChatEvent event) {
        event.renderer(chatRenderer);
    }
}
