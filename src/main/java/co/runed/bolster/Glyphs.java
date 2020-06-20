package co.runed.bolster;

import org.bukkit.ChatColor;

public class Glyphs {
    public static final String GOLD_COIN = glyph('\uE000');
    public static final String GOLD_INGOT = glyph('\uE001');
    public static final String GEM = glyph('\uE002');

    public static String glyph(char glyph) {
        return ChatColor.RESET.toString() + glyph;
    }
}
