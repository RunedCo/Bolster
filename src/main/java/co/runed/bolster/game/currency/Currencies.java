package co.runed.bolster.game.currency;

import co.runed.bolster.fx.Glyphs;

public class Currencies
{
    public static final Currency GOLD = new Currency("gold", "Gold", Glyphs.GOLD_COIN, false);
    public static final Currency EMERALD = new Currency("emerald", "Emerald", String.valueOf('\uE000'), true);
    public static final Currency DIAMOND = new Currency("diamond", "Diamond", String.valueOf('\uE000'), true);
}
