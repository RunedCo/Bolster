package co.runed.bolster.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ComponentUtil
{
    public static Collection<Component> wrappedText(String text)
    {
        List<Component> list = new ArrayList<>();

        if (text == null) return new ArrayList<>();

        String legacyText = LegacyComponentSerializer.legacyAmpersand().serialize(richText(text));

        List<String> lore = StringUtil.formatLore(legacyText);

        for (String line : lore)
        {
            list.add(Component.text(line));
        }

        return list;
    }

    public static Component richText(String text)
    {
        if (text == null) return Component.empty();

        return MiniMessage.get().parse(text);
    }
}
