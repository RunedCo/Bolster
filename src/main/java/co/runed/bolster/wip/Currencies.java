package co.runed.bolster.wip;

import co.runed.bolster.util.Glyphs;

public class Currencies
{
    public static final Currency GOLD = new Currency("gold", "Gold", Glyphs.GOLD_COIN, false);
    public static final Currency EMERALD = new Currency("emerald", "Emerald", String.valueOf('\uE000'), true);
    public static final Currency DIAMOND = new Currency("diamond", "Diamond", String.valueOf('\uE000'), true);
}
