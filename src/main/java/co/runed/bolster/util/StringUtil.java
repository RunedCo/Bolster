package co.runed.bolster.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringUtil {
    public static List<String> formatLore(String text) {
        return formatLore(text, 45);
    }

    public static List<String> formatLore(String text, int lineLength) {
        List<String> lore = new ArrayList<>();

        if(text == null) return lore;

        int length = lineLength + (text.length() - ChatColor.stripColor(text).length());

        String wrapped = WordUtils.wrap(text, length);
        String[] wrappedArray = wrapped.split("\r\n");

        String previousLine = null;
        for (String line : wrappedArray) {
            if(previousLine != null) {
                line = ChatColor.getLastColors(previousLine) + line;
            }

            line = line.trim();

            lore.add(line);

            previousLine = line;
        }

        return lore;
    }

    public static String repeat(String stringToRepeat, int numRepeats) {
        return new String(new char[numRepeats]).replace("\0", stringToRepeat);
    }

    public static int countMatches(String source, String find) {
        return StringUtils.countMatches(source, find);
    }
}
