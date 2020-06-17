package co.runed.bolster.util;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringUtil {
    public static List<String> formatLore(String text) {
        List<String> lore = new ArrayList<>();

        if(text == null) return lore;

        String wrapped = WordUtils.wrap(text, 45);
        String[] wrappedArray = wrapped.split("\r\n");

        String previousLine = null;
        for (String line : wrappedArray) {
            if(previousLine != null) {
                line = ChatColor.getLastColors(previousLine) + line;
            }

            lore.add(line);

            previousLine = line;
        }

        return lore;
    }
}
