package co.runed.bolster.managers;

import co.runed.bolster.util.lang.Lang;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class ChatManager extends Manager {
    private final ChatRenderer chatRenderer = new BolsterChatRenderer();

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

    public static class BolsterChatRenderer implements ChatRenderer {
        @Override
        public @NotNull Component render(@NotNull Player source, @NotNull Component sourceDisplayName, @NotNull Component message, @NotNull Audience viewer) {
            var preMessage = "<pre>" + MiniMessage.get().serialize(message) + "</pre>";
            var rank = "<bold>SJ</bold> ";//""<gold><bold>ADMIN</bold></gold> ";

            return Lang.key("chat.format")
                    .withReplacement("name", source.getName())
                    .withReplacement("message", preMessage)
                    .withReplacement("rank", rank)
                    .withReplacement("name_color", "#128dff")
                    .withReplacement("chat_color", "#96cafa")
                    .withReplacement("test", "%message%")
                    .toComponent();
        }
    }
}
