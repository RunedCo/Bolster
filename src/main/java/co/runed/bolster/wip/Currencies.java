package co.runed.bolster.wip;

import co.runed.bolster.Glyphs;

public class Currencies
{
    public static final Currency GOLD = new Currency("gold", "Gold", Glyphs.GOLD_COIN, false);
    public static final Currency EMERALD = new Currency("emerald", "Emerald", String.valueOf('\uE000'), true);
    public static final Currency DIAMOND = new Currency("diamond", "Diamond", String.valueOf('\uE000'), true);

    public static final Currency BONE = new Currency("bone", "Bone", String.valueOf('\uE000'), true);
    public static final Currency RAT_TAIL = new Currency("rat_tail", "Rat's Tail", String.valueOf('\uE000'), true);
    public static final Currency MONSTER_FANG = new Currency("monster_fang", "Monster Fang", String.valueOf('\uE000'), true);
    public static final Currency CURSED_BEADS = new Currency("cursed_beads", "Cursed Bead", String.valueOf('\uE000'), true);
    public static final Currency DEMON_FUR = new Currency("demon_fur", "Demon Fur", String.valueOf('\uE000'), false);
    public static final Currency GOLDEN_TEETH = new Currency("golden_teeth", "Golden Teeth", String.valueOf('\uE000'), false);
    public static final Currency ANCIENT_RELIC = new Currency("ancient_relic", "Ancient Relic", String.valueOf('\uE000'), true);
}
