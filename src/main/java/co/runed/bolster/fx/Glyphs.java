package co.runed.bolster.fx;

import net.md_5.bungee.api.ChatColor;

public class Glyphs {
    public static final String GOLD_COIN = coloredGlyph('\uE000');
    public static final String GOLD_INGOT = coloredGlyph('\uE001');
    public static final String GEM = coloredGlyph('\uE002');

    public static final char VERTICAL_RECTANGLE = '\u25AE';

    public static final char BULLET = '\u2022';
    public static final char ARROW = '\u25BA';
    public static final char CROSS = '\u274C';

    public static final char PIXEL_FIX = '\uF801';

    public static final char LOGO_BIG_LEFT = '\uE016';
    public static final char LOGO_BIG_RIGHT = '\uE017';

    public static final String LOGO = LOGO_BIG_LEFT + "" + PIXEL_FIX + "" + LOGO_BIG_RIGHT;

    public static String coloredGlyph(char glyph) {
        return coloredGlyph(ChatColor.WHITE, glyph);
    }

    public static String coloredGlyph(ChatColor color, char glyph) {
        return color.toString() + glyph + ChatColor.RESET;
    }
}
