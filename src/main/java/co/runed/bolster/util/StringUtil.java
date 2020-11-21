package co.runed.bolster.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

public class StringUtil
{
    public static final int LINE_LENGTH = 50;

    /**
     * Format a string to the default length, keeping {@link ChatColor} between lines
     *
     * @param text the text
     * @return
     */
    public static List<String> formatLore(String text)
    {
        return formatLore(text, LINE_LENGTH);
    }

    /**
     * Format a string to a list of a specific length, keeping {@link ChatColor} between lines
     *
     * @param text       the text
     * @param lineLength the maximum line length
     * @return
     */
    public static List<String> formatLore(String text, int lineLength)
    {
        List<String> lore = new ArrayList<>();

        if (text == null) return lore;

        String[] lines = text.split("\n");

        for (String line : lines)
        {
            lore.addAll(formatLine(line, lineLength));
        }

        return lore;
    }

    private static List<String> formatLine(String text, int lineLength)
    {
        List<String> lore = new ArrayList<>();

        int length = lineLength + (text.length() - ChatColor.stripColor(text).length());

        String wrapped = WordUtils.wrap(text, length, "\n", true);
        String[] wrappedArray = wrapped.split("\n");

        String previousLine = null;
        for (String line : wrappedArray)
        {
            line = ChatColor.translateAlternateColorCodes('&', line);

            if (previousLine != null)
            {
                line = ChatColor.getLastColors(previousLine) + line;
            }

            lore.add(line);

            previousLine = line;
        }

        return lore;
    }

    public static List<String> formatBullet(String text)
    {
        return formatBullet(text, LINE_LENGTH);
    }

    public static List<String> formatBullet(String text, int lineLength)
    {
        String bullet = "  " + '\u2022' + " ";

        List<String> lore = StringUtil.formatLore(text, lineLength - 4);
        List<String> out = new ArrayList<>();

        boolean firstLine = true;
        for (String line : lore)
        {
            String start = firstLine ? bullet : "    ";

            if (line.startsWith(String.valueOf(ChatColor.COLOR_CHAR)))
                start = ChatColor.COLOR_CHAR + "" + line.charAt(1) + start;

            out.add(start + line);

            firstLine = false;
        }

        return out;
    }


    public static List<String> formatQuote(String quote, String author)
    {
        List<String> quoteList = formatLore(ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + quote);

        quoteList.add(ChatColor.GRAY + "- " + author);

        return quoteList;
    }

    public static String join(String joiner, Collection<String> items)
    {
        StringBuilder out = new StringBuilder();

        for (String item : items)
        {
            out.append(item).append(joiner);
        }

        if (out.length() <= 0) return "YY";

        return out.substring(0, out.length() - joiner.length());
    }

    /**
     * Repeat a string a certain number of times
     *
     * @param string the string
     * @param count  the number of times to repeat
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

    public static boolean isInt(String s)  // assuming integer is in decimal number system
    {
        for (int a = 0; a < s.length(); a++)
        {
            if (a == 0 && s.charAt(a) == '-') continue;
            if (!Character.isDigit(s.charAt(a))) return false;
        }
        return true;
    }

    private final static TreeMap<Integer, String> romanNumeralMap = new TreeMap<Integer, String>();

    static
    {
        romanNumeralMap.put(1000, "M");
        romanNumeralMap.put(900, "CM");
        romanNumeralMap.put(500, "D");
        romanNumeralMap.put(400, "CD");
        romanNumeralMap.put(100, "C");
        romanNumeralMap.put(90, "XC");
        romanNumeralMap.put(50, "L");
        romanNumeralMap.put(40, "XL");
        romanNumeralMap.put(10, "X");
        romanNumeralMap.put(9, "IX");
        romanNumeralMap.put(5, "V");
        romanNumeralMap.put(4, "IV");
        romanNumeralMap.put(1, "I");
    }

    public static String toRoman(int number)
    {
        int l = romanNumeralMap.floorKey(number);

        if (number == l)
        {
            return romanNumeralMap.get(number);
        }

        return romanNumeralMap.get(l) + toRoman(number - l);
    }
}
