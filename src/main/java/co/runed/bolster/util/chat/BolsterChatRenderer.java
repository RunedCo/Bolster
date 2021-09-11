package co.runed.bolster.util.chat;

import co.runed.bolster.util.lang.Lang;
import io.papermc.paper.chat.ChatRenderer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BolsterChatRenderer implements ChatRenderer {
    @Override
    public @NotNull Component render(@NotNull Player source, @NotNull Component sourceDisplayName, @NotNull Component message, @NotNull Audience viewer) {
        var preMessage = "<pre>" + MiniMessage.get().serialize(message) + "</pre>";
        var rank = "<gold><bold>ADMIN</bold></gold> ";

        return Lang.key("chat.format")
                .replace("name", source.getName())
                .replace("message", preMessage)
                .replace("rank", rank)
                .replace("name_color", "#128dff")
                .replace("chat_color", "#96cafa")
                .replace("test", "%message%")
                .toComponent();
    }
}
