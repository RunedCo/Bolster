package co.runed.bolster.util;

import net.kyori.adventure.text.Component;

import java.util.List;

public class ComponentUtil
{
    public static Component wrappedText(String text)
    {
        Component component = Component.empty();

        List<String> lore = StringUtil.formatLore(text);

        for (String line : lore)
        {
            component = component.append(Component.text(line));
        }

        return component;
    }
}
