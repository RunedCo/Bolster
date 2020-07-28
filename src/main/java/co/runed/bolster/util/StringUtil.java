package co.runed.bolster.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class StringUtil
{
    /**
     * Format a string to the default length, keeping {@link ChatColor} between lines
     *
     * @param text the text
     * @return
     */
    public static List<String> formatLore(String text)
    {
        return formatLore(text, 45);
    }

    /**
     * Format a string to a list of a specific length, keeping {@link ChatColor} between lines
     *
     * @param text the text
     * @param lineLength the maximum line length
     * @return
     */
    public static List<String> formatLore(String text, int lineLength)
    {
        List<String> lore = new ArrayList<>();

        if (text == null) return lore;

        int length = lineLength + (text.length() - ChatColor.stripColor(text).length());

        String wrapped = WordUtils.wrap(text, length);
        String[] wrappedArray = wrapped.split("\r\n");

        String previousLine = null;
        for (String line : wrappedArray)
        {
            if (previousLine != null)
            {
                line = ChatColor.getLastColors(previousLine) + line;
            }

            line = line.trim();

            lore.add(line);

            previousLine = line;
        }

        return lore;
    }

    public static List<String> formatQuote(String quote, String author)
    {
        List<String> quoteList = formatLore(ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + quote);

        quoteList.add(ChatColor.GRAY + "- "+ author);

        return quoteList;
    }

    /**
     * Repeat a string a certain number of times
     *
     * @param string the string
     * @param count the number of times to repeat
     * @return
     */
    public static String repeat(String string, int count)
    {
        return new String(new char[count]).replace("\0", string);
    }

    public static int countMatches(String source, String find)
    {
        return StringUtils.countMatches(source, find);
    }
}
